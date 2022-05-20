package com.tungsten.hmclpe.launcher.uis.game.manager.right;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.game.World;
import com.tungsten.hmclpe.launcher.list.local.save.WorldListAdapter;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorldManagerUI extends BaseUI implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public LinearLayout worldManagerUI;

    private String versionName;
    private String version;
    private String saveDir;

    private CheckBox checkAll;
    private LinearLayout refresh;
    private LinearLayout addWorld;
    private LinearLayout download;

    private ProgressBar progressBar;
    private LinearLayout worldLayout;
    private ListView worldList;

    private ArrayList<World> allWorldList;
    private ArrayList<World> versionWorldList;

    public WorldManagerUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        worldManagerUI = activity.findViewById(R.id.ui_world_manager);

        checkAll = activity.findViewById(R.id.show_all_world);
        refresh = activity.findViewById(R.id.refresh_local_world);
        addWorld = activity.findViewById(R.id.add_new_world);
        download = activity.findViewById(R.id.download_new_world);

        checkAll.setOnCheckedChangeListener(this);
        refresh.setOnClickListener(this);
        addWorld.setOnClickListener(this);
        download.setOnClickListener(this);

        progressBar = activity.findViewById(R.id.load_world_progress);
        worldLayout = activity.findViewById(R.id.local_world_list_layout);
        worldList = activity.findViewById(R.id.world_list);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(worldManagerUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startWorldManager.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(worldManagerUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startWorldManager.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    public void refresh(String versionName){
        this.versionName = versionName;
        PrivateGameSetting privateGameSetting;
        String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg";
        if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
            privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
        }
        else {
            privateGameSetting = activity.privateGameSetting;
        }
        if (privateGameSetting.gameDirSetting.type == 0){
            saveDir = activity.launcherSetting.gameFileDirectory + "/saves";
        }
        else if (privateGameSetting.gameDirSetting.type == 1){
            saveDir = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/saves";
        }
        else {
            saveDir = privateGameSetting.gameDirSetting.path + "/saves";
        }
        refreshList();
    }

    private void refreshList() {
        new Thread(() -> {
            activity.runOnUiThread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                worldLayout.setVisibility(View.GONE);
            });
            allWorldList = new ArrayList<>();
            String gameJsonText = FileStringUtils.getStringFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName +"/" + versionName + ".json");
            Gson gson = JsonUtils.defaultGsonBuilder()
                    .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                    .registerTypeAdapter(Bits.class, new Bits.Serializer())
                    .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                    .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                    .create();
            Version v = gson.fromJson(gameJsonText, Version.class);
            this.version = v.getId();
            String[] strings = new File(saveDir + "/").list();
            if (strings != null) {
                for (String s : strings) {
                    try {
                        World world = new World(new File(saveDir + "/" + s).toPath());
                        allWorldList.add(world);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            activity.runOnUiThread(() -> {
                getNeededList();
                progressBar.setVisibility(View.GONE);
                worldLayout.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void getNeededList() {
        versionWorldList = new ArrayList<>();
        if (checkAll.isChecked()) {
            versionWorldList.addAll(allWorldList);
        }
        else {
            for (World world : allWorldList) {
                if (world.getGameVersion() == null || world.getGameVersion().equals(version)) {
                    versionWorldList.add(world);
                }
            }
        }
        WorldListAdapter adapter = new WorldListAdapter(context,versionWorldList);
        worldList.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view == refresh) {
            refreshList();
        }
        if (view == addWorld) {

        }
        if (view == download) {
            activity.uiManager.switchMainUI(activity.uiManager.downloadUI);
            activity.uiManager.downloadUI.downloadUIManager.switchDownloadUI(activity.uiManager.downloadUI.downloadUIManager.downloadWorldUI);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == checkAll) {
            getNeededList();
        }
    }
}
