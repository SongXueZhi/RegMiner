import { message } from 'antd';
import request from 'umi-request';
import type {
  CommentListItems,
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

// export async function getCriticalChangeByUuid(params: {
//   regression_uuid: string;
//   revision_name: 'bic' | 'bfc';
// }) {
//   const { code, msg, data } = await request<API.RegResponse<RegressionCriticalChangeDetail>>(
//     '/api/regression/criticalChange',
//     {
//       method: 'GET',
//       params,
//     },
//   );
//   if (code !== 200) {
//     message.error(msg);
//     return null;
//   } else {
//     return data;
//   }
// }

// 传参改为驼峰法
export async function postRegressionRevert(params: {
  projectFullName: string;
  regressionUuid: string;
  revisionName: string;
  filePath: string;
  userToken: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<any>>('/api/regression/revert', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    params: { ...params },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    message.success(msg);
    return data;
  }
}

export async function postRegressionUpdateNewCode(
  params: {
    projectFullName: string;
    userToken: string;
    regressionUuid: string;
    filePath: string;
    revisionName: string;
  },
  body: string,
) {
  const { code, msg, data } = await request<API.RegResponse<any>>('/api/regression/update', {
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
    message.success(msg);
    return data;
  }
}

export async function postClearCache(params: {
  userToken: string;
  projectFullName: string;
  regressionUuid: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<null>>('/api/regression/clearCache', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    params: { ...params },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    message.success(msg);
    return data;
  }
}

export async function getCommentList(params: { regression_uuid: string }) {
  const { code, msg, data } = await request<API.RegResponse<CommentListItems[]>>(
    '/api/regression/comments',
    {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      params: { ...params },
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    return data;
  }
}

export async function addComment(params: {
  regression_uuid: string;
  account_name: string;
  context: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<null>>('/api/regression/comments', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    params: { ...params },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    message.success(msg);
    return data;
  }
}

export async function deleteComment(params: {
  regression_uuid: string;
  account_name: string;
  comment_id: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<null>>('/api/regression/comments', {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    params: { ...params },
  });
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    message.success(msg);
    return data;
  }
}

export async function getRetrievalCriticalChangeReviewList(params: {
  regression_uuid: string;
  revision_name: string;
}) {
  const { code, msg, data } = await request<API.RegResponse<RegressionCriticalChangeDetail>>(
    '/api/regression/criticalChange/review',
    {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      params: { ...params },
    },
  );
  if (code !== 200) {
    message.error(msg);
    return null;
  } else {
    return data;
  }
}

// add && ground truth feedback function
export async function putCriticalChangeReviewById(
  params: {
    regression_uuid?: string;
    revision_name?: 'bic' | 'bfc';
    account_name?: string;
    feedback?: string;
    review_id?: number;
  },
  body: HunkEntityParams,
) {
  const { code, msg, data } = await request<API.RegResponse<null>>(
    '/api/regression/criticalChange/review',
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

// reject feedback function
export async function deleteCriticalChangeReviewById(params: {
  regression_uuid: string;
  revision_name: 'bic' | 'bfc';
  review_id: number;
}) {
  const { code, msg, data } = await request<API.RegResponse<RegressionCriticalChangeDetail>>(
    '/api/regression/criticalChange/review',
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
