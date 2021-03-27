package com.wku.wellcover;

public class Token {
    /**
     * @Token Verification Token验证
     * 1.Check whether token exists in the local(by Client)查看本地是否有token（客户端）
     *     Case 1:not have 没有
     *     2.Login by password and ask for auto-login or not 密码登录，询问是否保存登录状态（客户端）
     *     3.Password verification 验证密码（服务器）
     *
     *     Case 2:have 有
     *     2. Sending token 发送token（客户端）
     *     3. Verify token and timestamp token和时间戳验证 （服务器）
     *
     *     if auto-login
     *     4.Generate token patched with timestamp, sending to the client. (by Server)服务器返回token，加上时间戳，发给客户端（服务器）
     *     5.Save in the local (by Client) 接受保存token（客户端）
     *
     *     @issue Too Difficult 老麻烦了，建议先放着。
     *     @version 0.0
     */


}
