# -*- coding: utf-8 -*-

import sys
import logging
import json
import requests
import time
import random

logging.basicConfig(level=logging.INFO, stream=sys.stdout)
logger = logging.getLogger()
logger.setLevel(level=logging.INFO)
logger.info('Loading function')
TOKEN_STRING='小米推送AppSecret'

class dingDong(object):
    """docstring for dingDong"""
    def __init__(self,regid):
        super(dingDong, self).__init__()
        self.token_string = TOKEN_STRING
        self.headers = {"Authorization":"key={}".format(TOKEN_STRING)}
        self.regid=regid
    def sendBigmsg(self,title,mddata):
        '''
        大单条消息
        '''
        data={"description":mddata[:50],
            "payload":title,
            "restricted_package_name":"com.bigbig.ding",
            "title":title,
            "notify_type":"2",
            "time_to_live":"1000",
            "pass_through":"0",
            "notify_id":int(time.time()+random.uniform(0,1)),
            "registration_id":self.regid,
            "extra.notification_style_type":'1',
            "extra.notification_large_icon_uri":"http://t3.market.xiaomi.com/thumcrop/webp/h120/MiPass/0ba0443bd8603fe44a9857385b11d76932541b094/icon.webp?crop=l276r600",
            "extra.md":mddata
            }
        r = requests.post("https://api.xmpush.xiaomi.com/v3/message/regid", headers=self.headers,data=data)
        result=r.json()
        if result.get('code')==0:
            logger.info('发送成功')
            return True
        else:
            logger.info('发送失败')
            logger.info(result)
        return False

def main_handler(event,content):
    if "requestContext" not in event.keys():
        return {"errorCode":410,"errorMsg":"event is not come from api gateway"}
    if event["path"] != "/APIService-mipushServer/dd/sendmsg":
        return {"errorCode":411,"errorMsg":"request is not from setting api path"}
    if event["path"] == "/APIService-mipushServer/dd/sendmsg" and event["httpMethod"] in ["GET","POST"]: #获取文章列表
        try:
            regid = event["queryString"]["id"]
            if not regid:
                return {"errorCode":412,"errorMsg":"regid is null"}
            title = event["queryString"]["title"]
            msg = event["queryString"]["msg"]
            dd=dingDong(regid)    
            dd.sendBigmsg(title,msg[:800])
        except Exception as e:
            logger.info(e)
            return {"errorCode":412,"errorMsg":"error is happend"}
   
        return {"errorCode":200,"errorMsg":'regid:{} is send'.format(regid)}
    return {"errorCode":411,"errorMsg":"request is not work"}