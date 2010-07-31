Use TSP_SMS_ChinaMobileCMPP
GO

--  select * from [tb_Businesses]
 
-- INSERT INTO [tb_Businesses]   ([service_id] ,[service_name] ,[service_type] ,[fee_type] ,[fee_value] ,[buy_command] ,[abandon_command] ,[src_num] ,[platform_type] ,[build_time] ,[descript]) 
--		VALUES ('FREE', 'test_business', 1, 2, 500, 'DZ', 'QDZ', '106590001', 0, GETDATE(), '测试业务'  )
-- INSERT INTO [tb_Businesses]   ([service_id] ,[service_name] ,[service_type] ,[fee_type] ,[fee_value] ,[buy_command] ,[abandon_command] ,[src_num] ,[platform_type] ,[build_time] ,[descript]) 
--		VALUES ('TEST', 'test', 1, 3, 300, 'test', 'test', '10659000', 0, GETDATE(), '测试业务2'  )

--正式：
--		TRUNCATE TABLE [tb_Businesses]

INSERT INTO [tb_Businesses]   ([service_id] ,[service_name] ,[service_type] ,[fee_type] ,[fee_value] ,[buy_command] ,[abandon_command] ,[src_num] ,[platform_type] ,[build_time] ,[descript]) 
		VALUES ('MYN2119901', 'MYN2119901', 1, 0, 000, 'test', 'test', '106573488510', 0, GETDATE(), '测试业务2')

INSERT INTO [SM_ROUTE](frmNum,toNum,localServerIp,localServerPort)
values('13000000000','18999999999','192.168.1.1','80')

-- select * from [SM_ROUTE] where '15989019316'>=frmnum and '15989019316'<=tonum



--INSERT INTO [tb_Businesses]   ([service_id] ,[service_name] ,[service_type] ,[fee_type] ,[fee_value] ,[buy_command] ,[abandon_command] ,[src_num] ,[platform_type] ,[build_time] ,[descript]) 
--		VALUES ('TYN0919901', 'TYN0919901', 1, 0, 000, 'test', 'test', '106573050001', 0, GETDATE(), '测试业务2')


--INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
--[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
--[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
--[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
--		VALUES (2 ,1 ,0 ,-1 ,1 ,3 ,1 ,1 ,'10659000' ,'12345678900' ,
--'12345678900' ,'FREE' ,'2' ,1 ,'500' ,'测试下发短信111111111' ,
--datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'JMB' ,NULL ,NULL ,'' ,0,1,0,null,-1)
		
--  select  * from [tb_SMS_CMPP_MTs]

-- DELETE [tb_SMS_CMPP_MTs]   WHERE ID =1

-- TRUNCATE TABLE [tb_SMS_CMPP_MTs]

-- update [tb_SMS_CMPP_MTs] SET MSGID=2 WHERE ID =2

--正式：
--群发
INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,1 ,0 ,-1 ,0 ,3 ,0 ,1 ,'106573488510' ,'13888643054' ,
'13888643054' ,'MYN2119901' ,'01' ,0 ,'000' ,'群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试群发长短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425593' ,NULL ,NULL ,'' ,0,0,0,null,-1)

INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,2 ,0 ,-1 ,0 ,3 ,0 ,1 ,'106573488510' ,'13885583766' ,
'13885583766' ,'MYN2119901' ,'01' ,0 ,'000' ,'群发短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425593' ,NULL ,NULL ,'' ,0,0,0,null,-1)

INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,3 ,0 ,-1 ,0 ,3 ,0 ,1 ,'106573488510' ,'13908853857' ,
'13908853857' ,'MYN2119901' ,'01' ,0 ,'000' ,'群发短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425593' ,NULL ,NULL ,'' ,0,0,0,null,-1)

INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,4 ,0 ,-1 ,0 ,3 ,0 ,1 ,'106573488510' ,'15925183082' ,
'15925183082' ,'MYN2119901' ,'01' ,0 ,'000' ,'群发短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425593' ,NULL ,NULL ,'' ,0,0,0,null,-1)

INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,5 ,0 ,-1 ,0 ,3 ,0 ,1 ,'106573488510' ,'13987682588' ,
'13987682588' ,'MYN2119901' ,'01' ,0 ,'000' ,'群发短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425593' ,NULL ,NULL ,'' ,0,0,0,null,-1)

INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,6 ,0 ,-1 ,0 ,3 ,0 ,1 ,'106573488510' ,'13888528678' ,
'13888528678' ,'MYN2119901' ,'01' ,0 ,'000' ,'群发短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425593' ,NULL ,NULL ,'' ,0,0,0,null,-1)

--单发
INSERT INTO  [tb_SMS_CMPP_MTs] ([nSendLevel] ,[MsgId] ,[SendStatus] ,[SendResult] ,[SendCount] ,[MaxSendCount] ,
[StatusReportFlag] ,[MsgType] ,[OrgAddr] ,[DestAddr] ,[FeeAddr] ,[ServiceID] ,[FeeType] ,[FeeUserType] ,
[FeeCode] ,[UserData] ,[BuildTime] ,[SendTime] ,[SPID] ,[WapPushHttp] ,[WapPushContent] ,[link_id] ,
[expire_time],[sms_multi],[msg_fmt],[msg_port],[request_status])      
		VALUES (2 ,1 ,0 ,-1 ,0 ,3 ,1 ,1 ,'106573050001' ,'13888643054' ,
'13888643054' ,'TYN0919901' ,'01' ,0 ,'000' ,'群发短信测试，收到请回复，谢谢！' ,
datediff(second,'2004-01-01 00:00:00',getdate()) ,null ,'425111' ,NULL ,NULL ,'' ,0,1,0,null,-1)