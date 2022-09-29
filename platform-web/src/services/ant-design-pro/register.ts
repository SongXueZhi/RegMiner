import { message } from 'antd';
import { request } from 'umi';

// 注册接口 POST
export async function register(body: API.RegisterParams, options?: { [key: string]: any }) {
  return request<API.RegResponse<null>>('/api/account/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  }).then(({ code, msg, data }) => {
    if (code === 200) {
      message.success(msg);
      return data;
    } else {
      return code;
    }
  });
}
