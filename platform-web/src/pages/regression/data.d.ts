export type TableListItem = {
  key: number;
  disabled?: boolean;
  href: string;
  avatar: string;
  name: string;
  owner: string;
  desc: string;
  callNo: number;
  status: string;
  updatedAt: Date;
  createdAt: Date;
  progress: number;
  keyword?: any;
};

export type TableListPagination = {
  total: number;
  pageSize: number;
  current: number;
};

export type TableListData = {
  list: TableListItem[];
  pagination: Partial<TableListPagination>;
};

export type RegQueryParams = {
  regression_uuid?: string;
  regression_status?: number;
  project_full_name?: string;
  bugId?: string;
  bfc?: string;
  buggy?: string;
  bic?: string;
  work?: string;
  testcase?: string;
  pageSize?: number;
  currentPage?: number;
  filter?: Record<string, ReactText[] | null>;
  sorter?: Record<string, any>;
  keyword?: any;
};
