import {message} from 'antd';
import request from 'umi-request';
import type {RegressionCode, RegressionDetail} from './data';

export async function queryRegressionDetail(params: {
  regression_uuid: string;
  userToken: string;
  bic: string;
}) {
  const {code, msg, data} = await request<API.RegResponse<RegressionDetail>>(
    '/api/regression/migrate',
    {
      method: 'GET',
      params,
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}

export async function regressionCheckout(params: { regression_uuid: string; userToken: string }) {
  const {code, msg, data} = await request<API.RegResponse<any>>('/api/regression/checkout', {
    method: 'PUT',
    params,
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}

export async function queryRegressionCode(params: {
  regression_uuid: string;
  filename?: string;
  userToken?: string;
  old_path: string;
  new_path: string;
  revisionFlag: string;
}) {
  const {code, msg, data} = await request<API.RegResponse<RegressionCode>>(
    '/api/regression/code',
    {
      method: 'GET',
      params,
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}

export async function getRegressionPath(params: {
  regression_uuid: string;
  revisionFlag: string;
  userToken: string;
}) {
  const {code, msg, data} = await request<API.RegResponse<string>>('/api/regression/test', {
    method: 'GET',
    params,
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}

export async function getRegressionConsole(params: { path: string }) {
  const {code, msg, data} = await request<API.RegResponse<string>>('/api/regression/console', {
    method: 'GET',
    params,
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}
