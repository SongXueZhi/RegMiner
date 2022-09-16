import { message } from 'antd';
import request from 'umi-request';
import type {
  HunkEntityParams,
  RegressionCode,
  RegressionCriticalChangeDetail,
  RegressionDetail,
} from './data';

export async function queryRegressionDetail(params: {
  regression_uuid: string;
  userToken: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<RegressionDetail>>(
    '/api/regression/detail',
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
  const { code, msg, data } = await request<API.RegResponse<any>>('/api/regression/checkout', {
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
  filename: string;
  userToken?: string;
  old_path: string;
  new_path: string;
  revisionFlag: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<RegressionCode>>(
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
  const { code, msg, data } = await request<API.RegResponse<string>>('/api/regression/test', {
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
  const { code, msg, data } = await request<API.RegResponse<string>>('/api/regression/console', {
    method: 'GET',
    params,
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  }
  return data;
}

export async function getCriticalChangeByUuid(params: {
  regression_uuid: string;
  revision_name: 'bic' | 'bfc';
}) {
  const { code, msg, data } = await request<API.RegResponse<RegressionCriticalChangeDetail>>(
    '/api/regression/criticalChange',
    {
      method: 'GET',
      params,
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    return data;
  }
}

export async function putCriticalChangeByUuid(
  params: {
    regression_uuid?: string;
    revision_name?: 'bic' | 'bfc';
  },
  body: HunkEntityParams,
) {
  const { code, msg, data } = await request<API.RegResponse<null>>(
    '/api/regression/criticalChange',
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      params,
      data: body,
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    return data;
  }
}

export async function deleteCriticalChangeById(params: {
  regression_uuid: string;
  revision_name: 'bic' | 'bfc';
  critical_change_id: number;
}) {
  const { code, msg, data } = await request<API.RegResponse<RegressionCriticalChangeDetail>>(
    '/api/regression/criticalChange',
    {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
      params,
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    return data;
  }
}

export async function postRegressionCodeModified(
  params: {
    userToken: string;
    regression_uuid: string;
    old_path: string;
    revision_name: string;
    // new_code?: string;
    cover_status: 0 | 1; // 0 - reset the code to original, 1 - cover with new code
  },
  body: string,
) {
  const { code, msg, data } = await request<API.RegResponse<any>>('/api/regression/modified', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    params: { ...params },
    body: body,
    // data: { body },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    return data;
  }
}
