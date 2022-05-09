from lanzou.api import LanZouCloud
import sys,os,json,demjson
cookie=dict()
def decodeCookie(raws):
    kavs=raws.split("; ")
    for st in kavs:
        k,v=st.split("=")
        cookie[k]=v
rawdata=repr(sys.argv[1])
rawda="""_uab_collina=165201861613045099432833;
phpdisk_info=BDJWZVU2ADQBMQ9pDG8EVwBkBg0PZ1c4U2gGZ1JkBzVYaVVmB20ANVNmA1pePVZuBWdWZFk1XTIDMFdhBTJQawQ2VjVVYQA9AWEPawxjBG4AaAY1D2VXZlNpBjNSMgc2WGlVZQdnAG1TYQNmXg1WPQVtVmxZMV06AzBXMgUyUGoEOVZj; 
uag=b51eb3787f411f4c412fa9d4e815cdc6; 
ylogin=2118269; 
UM_distinctid=180a3fbf1e7530-0ac6be4e10d7d5-4c647e5c-13c680-180a3fbf1e87cd; 
folder_id_c=-1; 
PHPSESSID=371caate45730opvskgj6ke8abnmrcg6; 
CNZZDATA1253610886=100400028-1652009219-https%253A%252F%252Fpc.woozooo.com%252F%7C1652058187
"""
decodeCookie(rawdata)
#print("Not Decoded yet data:\033[33m",jsons,end='\033[0m\n')
#exit()
#ckie=demjson.decode(jsons)
#ckie=json.loads(ckie)

#print("Decoded: \033[34m",ckie,"\033[0m")

#cookie={'PHPSESSID':'eild6pmj7gp1vcnese4435njobnf2k3j','phpdisk_info':'BDJWZVU2ADQBMQ9pDG8EVwBkBg0PZ1c4U2gGZ1JkBzVYaVVmB20ANVNmA1pePVZuBWdWZFk1XTIDMFdhBTJQawQ2VjVVYQA9AWEPawxjBG4AaAY1D2VXZlNpBjNSMgc2WGlVZQdnAG1TYQNmXg1WPQVtVmxZMV06AzBXMgUyUGoEOVZj','ylogin':'2118269'}
lzy = LanZouCloud()
lzy.ignore_limits()
code = lzy.login_by_cookie(cookie)
if code == LanZouCloud.SUCCESS:
    print('登录成功')
#print(lzy.get_file_list())
#info=lzy.get_share_info(69771324)
#print("Class:",type(info))
#print(info)
#print("URL:",info[2])
'''
def show_progress(file_name, total_size, now_size):
    """显示进度的回调函数"""
    percent = now_size / total_size
    bar_len = 40  # 进度条长总度
    bar_str = '>' * round(bar_len * percent) + '=' * round(bar_len * (1 - percent))
    print('\r{:.2f}%\t[{}] {:.1f}/{:.1f}MB | {} '.format(
        percent * 100, bar_str, now_size / 1048576, total_size / 1048576, file_name), end='')
    if total_size == now_size:
       print('')  # 下载完成换行
'''
def handler(fid, is_file):
    print("Uploaded~~")
    if is_file:
        lzy.set_desc(fid, 'AUTOBUILD by zhangjing-github-code(sign changed)', is_file=True)
    info=lzy.get_share_info(69771324)
    #print("Class:",type(info))
    #print(info)
    print("Share URL:",info[2])
code=lzy.upload_file(r"HMCLPE/build/outputs/apk/release/HMCLPE-release.apk",-1,uploaded_handler=handler)
print(lzy.get_file_list())
print("Return code:",code)
