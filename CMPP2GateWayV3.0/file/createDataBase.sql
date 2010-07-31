
/******************************************************************************
 *Copyright(c)2010-2030 JMB-WorkRoom 版权所有
 * 
 *功能：移动CMPP2数据库脚本
 *
 *创建：Joe Chen 2010-7-19
 *******************************************************************************/




IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'TSP_SMS_ChinaMobileCMPP')
	DROP DATABASE [TSP_SMS_ChinaMobileCMPP]
GO

CREATE DATABASE [TSP_SMS_ChinaMobileCMPP]   COLLATE Chinese_PRC_CI_AS
GO
use [TSP_SMS_ChinaMobileCMPP]
GO


/****** Object:  Table [dbo].[tb_Businesses]    Script Date: 2010-7-19 4:51:51 ******/
CREATE TABLE [dbo].[tb_Businesses] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[service_id] [varchar] (20) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[service_name] [varchar] (40) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[service_type] [smallint] NOT NULL ,
	[fee_type] [smallint] NOT NULL ,
	[fee_value] [smallint] NOT NULL ,
	[buy_command] [varchar] (20) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[abandon_command] [varchar] (20) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[src_num] [varchar] (21) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[platform_type] [smallint] NULL ,
	[build_time] [datetime] NOT NULL default(getdate()) ,
	[descript] [varchar] (400) COLLATE Chinese_PRC_CI_AS NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[tb_SMS_CMPP_MTMO]  *****/

CREATE TABLE [dbo].[tb_SMS_CMPP_MTMO] (
	[id] [bigint] IDENTITY (1, 1) NOT NULL ,
	[msg_id] [bigint] NULL ,
	[local_msg_id] [bigint] NULL ,
	[send_level] [smallint] NOT NULL default(0),
	[user_mobile] [bigint] Not NULL ,
	[fee_mobile] [bigint] NOT NULL ,
	[msg_content] [varchar] (1000) COLLATE Chinese_PRC_CI_AS NULL ,
	[src_num] [varchar(25)] NOT NULL ,
	[sent_time] [int] NOT NULL ,
	[arrived_time] [int] NULL ,
	[mt_result] [int] NOT NULL ,
	[service_id] [int] NOT NULL ,
	[msg_type] [smallint] NOT NULL ,
	[fee_value] [smallint] NOT NULL ,
	[sp_id] [int] NOT NULL,
	[sms_multi] [int] NOT NULL DEFAULT(1),--是否是群发 0:群发 1:逐一发送
	[msg_fmt] [int] NOT NULL DEFAULT(0),--0是非端口短信 1是端口短信
	[msg_port] [varchar](10),--端口短信端口号 
	[request_status] [int] NOT NULL DEFAULT(-1)--未请求服务接口：-1 请求成功：0 请求失败：1
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[tb_SMS_CMPP_MTReports]  ******/

CREATE TABLE [dbo].[tb_SMS_CMPP_MTReports] (
	[id] [bigint] IDENTITY (1, 1) NOT NULL ,
	[msg_id] [bigint]  NULL ,
	[user_mobile] [bigint] NOT NULL ,
	[status] [smallint] NOT NULL ,
	[recv_time] [int] NOT NULL DEFAULT (datediff(second,'2004-01-01 00:00:00',getdate())),
	[arrived_time] [int] NOT NULL DEFAULT (datediff(second,'2004-01-01 00:00:00',getdate())) 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[tb_SMS_CMPP_MTs]    ******/

CREATE TABLE [dbo].[tb_SMS_CMPP_MTs] (
	[ID] [bigint] IDENTITY (1, 1) NOT NULL ,
	[nSendLevel] [smallint] NOT NULL default(0),
	[MsgId] [bigint]  NULL ,
	[local_msg_id] [bigint] NULL ,
	[SendStatus] [smallint] NOT NULL default(0),
	[SendResult] [int] NOT NULL default(-1),
	[SendCount] [smallint] NOT NULL default(0),
	[MaxSendCount] [smallint] NOT NULL default(3),
	[StatusReportFlag] [smallint] NOT NULL default(1),
	[MsgType] [smallint] NOT NULL default(1),
	[OrgAddr] [varchar] (21) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[DestAddr] [varchar] (21) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[FeeAddr] [varchar] (21) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[ServiceID] [varchar] (20) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[FeeType] [varchar] (2) COLLATE Chinese_PRC_CI_AS NOT NULL DEFAULT(0),
	[FeeUserType] [smallint] NOT NULL DEFAULT(1),
	[FeeCode] [varchar] (6) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[UserData] [varchar] (1000) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[BuildTime] [int] NOT NULL DEFAULT (datediff(second,'2004-01-01 00:00:00',getdate())) ,
	[SendTime] [int] NULL DEFAULT (datediff(second,'2004-01-01 00:00:00',getdate())),
	[SPID] [varchar] (6) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[WapPushHttp] [varchar] (100) COLLATE Chinese_PRC_CI_AS NULL ,
	[WapPushContent] [varchar] (140) COLLATE Chinese_PRC_CI_AS NULL ,
	[link_id] [varchar] (20) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[expire_time] [int] NOT NULL default(0),
	[sms_multi] [int] NOT NULL DEFAULT(1),--是否是群发 0:群发 1:逐一发送
	[msg_fmt] [int] NOT NULL DEFAULT(0),--0是非端口短信 1是端口短信
	[msg_port] [varchar](10) NULL,--端口短信端口号
	[request_status] [int] NOT NULL DEFAULT(-1)--未请求服务接口：-1 请求成功：0 请求失败：1
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[tb_SMS_CMPP_MOs]   ******/

CREATE TABLE [dbo].[tb_SMS_CMPP_MOs] (
	[id] [bigint] IDENTITY (1, 1) NOT NULL ,
	[msg_id] [bigint]  NULL ,
	[dest_addr] [varchar] (21) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[src_addr] [bigint] NOT NULL ,
	[msg_content] [varchar] (140) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[service_id] [varchar] (20) COLLATE Chinese_PRC_CI_AS NULL ,
	[link_id] [varchar] (20) COLLATE Chinese_PRC_CI_AS NOT NULL ,
	[recv_time] [int] NOT NULL DEFAULT (datediff(second,'2004-01-01 00:00:00',getdate()))
) ON [PRIMARY]
GO


CREATE TABLE [dbo].[SM_ROUTE](
[RecID] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
[frmNum] [varchar](15) COLLATE Chinese_PRC_CI_AS NOT NULL,
[toNum] [varchar](15) COLLATE Chinese_PRC_CI_AS NOT NULL,
[localServerIp] [varchar](20) COLLATE Chinese_PRC_CI_AS NOT NULL,
[localServerPort] [int] NULL)





 CREATE  CLUSTERED  INDEX [Idx_tb_SMS_CMPP_MTs_nSendLevel] ON [dbo].[tb_SMS_CMPP_MTs]([nSendLevel]) WITH  FILLFACTOR = 100 ON [PRIMARY]
GO

 CREATE  INDEX [Idx_tb_SMS_CMPP_MTs_ID] ON [dbo].[tb_SMS_CMPP_MTs]([ID]) WITH  FILLFACTOR = 100 ON [PRIMARY]
GO


/****** Object:  Stored Procedure dbo.tsp_SMS_CMPP_MOs    ******/

CREATE    PROCEDURE [dbo].[tsp_SMS_CMPP_MOs]
	@MsgId bigint,
	@DestAddr varchar(21),
	@SrcAddr varchar(21), 
	@MsgContent varchar(140),
	@ServiceId varchar(20),
	@LinkId varchar(20)
AS
BEGIN
	Declare @user_mobile bigint
	if len(@SrcAddr)>11 begin
		set @user_mobile = cast(right(@SrcAddr,11) as bigint)
	end else begin
		set @user_mobile=cast(@SrcAddr as bigint)
	end

	insert into tb_SMS_CMPP_MOs (msg_id, dest_addr, src_addr, service_id,msg_content,link_id) 
		values (@MsgId,@DestAddr,@user_mobile,@ServiceId,@MsgContent,@LinkId);
END

GO


/****** Object:  Stored Procedure dbo.tsp_SMS_CMPP_MTReports    ******/


create    PROCEDURE tsp_SMS_CMPP_MTReports
  @nMsgId bigint,@strUserNum varchar(21),@nStatus smallint
AS

BEGIN
	Declare @user_mobile bigint
	if len(@strUserNum)>11 begin
	set @user_mobile = cast(right(@strUserNum,11) as bigint)
	end else begin
	set @user_mobile=cast(@strUserNum as bigint)
	end
	insert into tb_SMS_CMPP_MTReports (msg_id ,user_mobile ,status) values (@nMsgId,@user_mobile,@nStatus);
END

GO