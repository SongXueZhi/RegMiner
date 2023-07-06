import { message } from 'antd';
import request from 'umi-request';
import type { AllBugTypes, RegQueryParams } from './data';

export async function queryRegressionList(params?: RegQueryParams) {
  const { code, msg, data } = await request<API.RegResponse<API.RegressionItem[]>>(
    '/api/regression/all',
    {
      params,
    },
  );
  if (code !== 200) {
    message.error(msg);
    return {
      data: [],
      success: true,
      total: 0,
    };
  }
  return {
    data: data.map((resp, index) => {
      resp.index = index;
      return resp;
    }),
    success: true,
    total: data.length,
  };
}

export async function removeRegression(params: { regressionUuid: string }) {
  return request(`/api/regression/delete?regression_uuid=${params.regressionUuid}`, {
    method: 'DELETE',
  });
}

export async function addRegression(params: API.RegressionItem) {
  return request('/api/regression/add', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function updateStatus(params: { regressionUuid: string; regressionStatus: number }) {
  return request(
    `/api/regression/status?regression_uuid=${params.regressionUuid}&regression_status=${params.regressionStatus}`,
    {
      method: 'PUT',
    },
  );
}

export async function getAllBugTypes() {
  const { code, msg, data } = await request<API.RegResponse<AllBugTypes[]>>('/api/bugType/all', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}
