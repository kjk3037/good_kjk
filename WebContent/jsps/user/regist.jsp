<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>注册</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">

	<link rel="stylesheet" type="text/css" href="styles.css">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/css.css'/>">
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/user/regist.css'/>">
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/common.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jsps/js/user/regist.js'/>"></script>
  	<script type="text/javascript">
  	/*
  	 * 初始化时要执行的内容：
  	 */
  	$(function() {
  		/*
  		 * 1. 让注册按钮得到和失败光标时切换图片
  		 */
  		$("#submit").hover(
  			function() {
  				$("#submit").attr("src", "/good_re/images/regist2.jpg");
  			},
  			function() {
  				$("#submit").attr("src", "/good_re/images/regist1.jpg");
  			}
  		);
  		
  		/*
  		 * 2. 给注册按钮添加submit()事件，完成表单校验
  		 */
  		$("#submit").submit(function(){
  			var bool = true;
  			$(".input").each(function() {
  				var inputName = $(this).attr("name");
  				bool = invokeValidateFunction(inputName);
  			})
  			return bool;
  		});	
  		/*
  		 * 3. 输入框得到焦点时隐藏错误信息
  		 */
  		$(".input").focus(function() {
  			var inputName = $(this).attr("name");
  			$("#" + inputName + "Error").css("display", "none");
  		});
  		
  		/*
  		 * 4. 输入框推动焦点时进行校验
  		 */
  		$(".input").blur(function() {
  			var inputName = $(this).attr("name");
  			invokeValidateFunction(inputName);
  		})
  	});

  	/*
  	 * 输入input名称，调用对应的validate方法。
  	 * 例如input名称为：loginname，那么调用validateLoginname()方法。
  	 */
  	function invokeValidateFunction(inputName) {
  		inputName = inputName.substring(0, 1).toUpperCase() + inputName.substring(1);
  		var functionName = "validate" + inputName;
  		return eval(functionName + "()");	
  	}

  	/*
  	 * 校验登录名
  	 */
  	function validateLoginname() {
  		var bool = true;
  		$("#loginnameError").css("display", "none");
  		var value = $("#loginname").val();
  		if(!value) {// 非空校验
  			$("#loginnameError").css("display", "");
  			$("#loginnameError").text("用户名不能为空！");
  			bool = false;
  		} else if(value.length < 3 || value.length > 20) {//长度校验
  			$("#loginnameError").css("display", "");
  			$("#loginnameError").text("用户名长度必须在3 ~ 20之间！");
  			bool = false;
  		} else {// 是否被注册过
  			$.ajax({
  				cache: false,
  				async: false,
  				type: "POST",
  				dataType: "json",
  				data: {method: "validateLoginname", loginname: value},
  				url: "/good_re/UserServlet",
  				success: function(flag) {
  					if(flag) {
  						$("#loginnameError").css("display", "");
  						$("#loginnameError").text("用户名已被注册！");
  						bool = false;				
  					}
  				}
  			});
  		}
  		return bool;
  	}

  	/*
  	 * 校验密码
  	 */
  	function validateLoginpass() {
  		var bool = true;
  		$("#loginpassError").css("display", "none");
  		var value = $("#loginpass").val();
  		if(!value) {// 非空校验
  			$("#loginpassError").css("display", "");
  			$("#loginpassError").text("密码不能为空！");
  			bool = false;
  		} else if(value.length < 3 || value.length > 20) {//长度校验
  			$("#loginpassError").css("display", "");
  			$("#loginpassError").text("密码长度必须在3 ~ 20之间！");
  			bool = false;
  		}
  		return bool;
  	}

  	/*
  	 * 校验确认密码
  	 */
  	function validateReloginpass() {
  		var bool = true;
  		$("#reloginpassError").css("display", "none");
  		var value = $("#reloginpass").val();
  		if(!value) {// 非空校验
  			$("#reloginpassError").css("display", "");
  			$("#reloginpassError").text("确认密码不能为空！");
  			bool = false;
  		} else if(value != $("#loginpass").val()) {//两次输入是否一致
  			$("#reloginpassError").css("display", "");
  			$("#reloginpassError").text("两次密码输入不一致！");
  			bool = false;
  		}
  		return bool;	
  	}

  	/*
  	 * 校验Email
  	 */
  	function validateEmail() {
  		var bool = true;
  		$("#emailError").css("display", "none");
  		var value = $("#email").val();
  		if(!value) {// 非空校验
  			$("#emailError").css("display", "");
  			$("#emailError").text("Email不能为空！");
  			bool = false;
  		} else if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)) {//格式校验
  			$("#emailError").css("display", "");
  			$("#emailError").text("错误的Email格式！");
  			bool = false;
  		} else {//Email是否被注册过
  			$.ajax({
  				cache: false,
  				async: false,
  				type: "POST",
  				dataType: "json",
  				data: {method: "validateEmail", email: value},
  				url: "/good_re/UserServlet",
  				success: function(flag) {
  					if(flag) {
  						$("#emailError").css("display", "");
  						$("#emailError").text("Email已被注册！");
  						bool = false;					
  					}
  				}
  			});		
  		}
  		return bool;	
  	}

  	/*
  	 * 校验验证码
  	 */
  	function validateVerifyCode() {
  		var bool = true;
  		$("#verifyCodeError").css("display", "none");
  		var value = $("#verifyCode").val();
  		if(!value) {//非空校验
  			$("#verifyCodeError").css("display", "");
  			$("#verifyCodeError").text("验证码不能为空！");
  			bool = false;
  		} else if(value.length != 4) {//长度不为4就是错误的
  			$("#verifyCodeError").css("display", "");
  			$("#verifyCodeError").text("错误的验证码！");
  			bool = false;
  		} else {//验证码是否正确
  			$.ajax({
  				cache: false,
  				async: false,
  				type: "POST",
  				dataType: "json",
  				data: {method: "validateVerifyCode", verifyCode: value},
  				url: "/good_re/UserServlet",
  				success: function(flag) {
  					if(!flag) {
  						$("#verifyCodeError").css("display", "");
  						$("#verifyCodeError").text("错误的验证码！");
  						bool = false;					
  					}
  				}
  			});
  		}
  		return bool;
  	}

</script>
  </head>
  
  <body>
<div class="divBody">
  <div class="divTitle">
    <span class="spanTitle">新用户注册</span>
  </div>
  <div class="divCenter">
    <form action="<c:url value='/UserServlet'/>" method="post">
    <input type="hidden" name="method" value="regist"/>
    <table>
      <tr>
        <td class="tdLabel">用户名：</td>
        <td class="tdInput">
          <input type="text" name="loginname" id="loginname" class="input" value=""/>
        </td>
        <td class="tdError">
          <label class="labelError" id="loginnameError">用户名不能为空！</label>
        </td>
      </tr>
      <tr>
        <td class="tdLabel">登录密码：</td>
        <td class="tdInput">
          <input type="password" name="loginpass" id="loginpass" class="input" value=""/>
        </td>
        <td class="tdError">
          <label class="labelError" id="loginpassError"></label>
        </td>
      </tr>
      <tr>
        <td class="tdLabel">确认密码：</td>
        <td class="tdInput">
          <input type="password" name="reloginpass" id="reloginpass" class="input" value=""/>
        </td>
        <td class="tdError">
          <label class="labelError" id="reloginpassError"></label>
        </td>
      </tr>
      <tr>
        <td class="tdLabel">Email：</td>
        <td class="tdInput">
          <input type="text" name="email" id="email" class="input" value=""/>
        </td>
        <td class="tdError">
          <label class="labelError" id="emailError"></label>
        </td>
      </tr>
      <tr>
        <td class="tdLabel">图形验证码：</td>
        <td class="tdInput">
          <input type="text" name="verifyCode" id="verifyCode" class="input" value=""/>
        </td>
        <td class="tdError">
          <label class="labelError" id="verifyCodeError"></label>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <span class="verifyCodeImg"><img id="vCode" width="100" src="<c:url value='/VerifyCodeServlet'/>" /></span>
        </td>
        <td><a href="javascript: _change()">换一张</a></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <input type="image" src="<c:url value='/images/regist1.jpg'/>" id="submit"/>
        </td>
        <td>&nbsp;</td>
      </tr>
    </table>
    </form>
  </div>
</div>
  </body>
</html>
	