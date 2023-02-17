import request from 'umi-request';
import { message } from 'antd';
import type { DdResultItems, RunDDByStepInputParams, RunDDInputParams } from './data';

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

export async function runDeltaDebuggingByStep(params: RunDDByStepInputParams) {
  const { code, msg, data } = await request<API.RegResponse<DdResultItems>>('/api/dd/runDDStep', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: { ...params },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}
