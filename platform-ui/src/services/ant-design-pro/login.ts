// @ts-ignore
/* eslint-disable */
import { request } from 'umi';
import { handleResponse } from '../response';

// /** 发送验证码 POST /api/login/captcha */
// export async function getFakeCaptcha(
//   params: {
//     // query
//     /** 手机号 */
//     phone?: string;
//   },
//   options?: { [key: string]: any },
// ) {
//   return request<API.FakeCaptcha>('/api/login/captcha', {
//     method: 'GET',
//     params: {
//       ...params,
//     },
//     ...(options || {}),
//   });
// }

/** 登录接口 POST /api/account/login */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.RegResponse<API.LoginResult | null>>('/api/account/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  }).then(handleResponse);
}

/** 登出接口 POST /api/account/outLogin */
export async function outLogin(options?: { [key: string]: any }) {
  return request<API.RegResponse<Record<string, any>>>('/api/account/outLogin', {
    method: 'GET',
    ...(options || {}),
  }).then(handleResponse);
}

/** 获取当前的用户 GET /api/account/currentUser */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.RegResponse<API.CurrentUser | null>>('/api/account/currentUser', {
    method: 'GET',
    ...(options || {}),
  }).then(handleResponse);
}
