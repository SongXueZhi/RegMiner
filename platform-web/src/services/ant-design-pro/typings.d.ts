// @ts-ignore
/* eslint-disable */

declare namespace API {
  type RegResponse<T> = {
    code: number;
    msg: string;
    data: T;
  };

  type CurrentUser = {
    accountId: number;
    accountName: string;
    role: string;
    avatar?: string;
    email?: string;
    token?: string;
    // name?: string;
    // userid?: string;
    // signature?: string;
    // title?: string;
    // group?: string;
    // tags?: { key?: string; label?: string }[];
    // notifyCount?: number;
    // unreadCount?: number;
    // country?: string;
    // access?: string;
    // geographic?: {
    //   province?: { label?: string; key?: string };
    //   city?: { label?: string; key?: string };
    // };
    // address?: string;
    // phone?: string;
  };

  interface LoginParams {
    accountName: string;
    password: string;
    autoLogin?: boolean;
    type?: string;
  }

  interface RegisterParams {
    accountName: string;
    password: string;
    avatar?: string;
    email?: string;
    role: string;
  }

  type LoginResult = {
    accountId: number;
    accountName: string;
    role: string;
    avatar?: string;
    email?: string;
    token: string;
  };

  type RegressionItem = {
    index: number; // 前端设置的index，后端无此返回值
    regressionUuid: string;
    regressionStatus?: number;
    projectFullName?: string;
    bugId?: string;
    bfc?: string;
    buggy?: string;
    bic?: string;
    work?: string;
    testcase?: string;
  };

  type PageParams = {
    current?: number;
    pageSize?: number;
  };

  type RuleListItem = {
    key?: number;
    disabled?: boolean;
    href?: string;
    avatar?: string;
    name?: string;
    owner?: string;
    desc?: string;
    callNo?: number;
    status?: number;
    updatedAt?: string;
    createdAt?: string;
    progress?: number;
  };

  type RuleList = {
    data?: RuleListItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type FakeCaptcha = {
    code?: number;
    status?: string;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type NoticeIconList = {
    data?: NoticeIconItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type NoticeIconItemType = 'notification' | 'message' | 'event';

  type NoticeIconItem = {
    id?: string;
    extra?: string;
    key?: string;
    read?: boolean;
    avatar?: string;
    title?: string;
    status?: string;
    datetime?: string;
    description?: string;
    type?: NoticeIconItemType;
  };
}
