--业务表
TRUNCATE TABLE [tb_Businesses]


INSERT INTO [tb_Businesses]   ([service_id] ,[service_name] ,[service_type] ,[fee_type] ,[fee_value] ,[buy_command] ,[abandon_command] ,[src_num] ,[platform_type] ,[build_time] ,[descript]) 
		VALUES ('TYN0919901', 'TYN0919901', 1, 0, 000, 'test', 'test', '106573050001', 0, GETDATE(), '测试业务2')

--请求地址路由表
INSERT INTO [SM_ROUTE](frmNum,toNum,localServerIp,localServerPort)
values('13000000000','18999999999','192.168.1.1','80')

--短信发送表

TRUNCATE TABLE [tb_SMS_CMPP_MTs]

--端口短信

INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel],[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,0 ,-1 ,0 ,3 ,1 ,1 ,'0001' ,'13888643054' ,
'13888643054' ,'TYN0919901' ,'01' ,0 ,'000' ,'~M1@G端口短信测试，请回复！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425111' ,NULL ,NULL ,'' ,0,1,1,1231,-1)

--单发普通短信
INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2,0 ,-1 ,0 ,3 ,1 ,1 ,'0001' ,'13888643054' ,
'13888643054' ,'TYN0919901' ,'01' ,0 ,'000' ,'普通短信' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425111' ,NULL ,NULL ,'' ,0,1,0,null,-1)


--群发普通短信
INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2,0 ,-1 ,0 ,3 ,1 ,1 ,'0001' ,'13888643054' ,
'13888643054' ,'TYN0919901' ,'01' ,0 ,'000' ,'普通短信' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425111' ,NULL ,NULL ,'' ,0,0,0,null,-1)

--单发长短信
INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2,0 ,-1 ,0 ,3 ,1 ,1 ,'0001' ,'13888643054' ,
'13888643054' ,'TYN0919901' ,'01' ,0 ,'000' ,'长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425111' ,NULL ,NULL ,'' ,0,1,0,null,-1)


--群发长短信
INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2,0 ,-1 ,0 ,3 ,1 ,1 ,'0001' ,'13888643054' ,
'13888643054' ,'TYN0919901' ,'01' ,0 ,'000' ,'长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试长短信测试' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425111' ,NULL ,NULL ,'' ,0,1,0,null,-1)
