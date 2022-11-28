import request from 'umi-request';
import { message } from 'antd';
import type { DdResultItems, RunDDInputParams } from './data';

export async function runDeltaDebugging(params: RunDDInputParams) {
  const { code, msg, data } = await request<API.RegResponse<DdResultItems>>('/api/dd/runDD', {
    method: 'GET',
    params,
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}
