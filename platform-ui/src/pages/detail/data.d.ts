import { List } from 'lodash';

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

export type DiffEditDetailItems = {
  beginA: number;
  beginB: number;
  endA: number;
  endB: number;
  lengthA: number;
  lengthB: number;
  empty: boolean;
  type: string;
};

export type CommitItem = {
  editList: DiffEditDetailItems[];
  filename: string;
  newPath: string;
  oldPath: string;
  match?: number;
  type?: string;
};

export type RegressionDetail = {
  regressionUuid: string;
  projectFullName?: string;
  bfc: string;
  bic: string;
  bicURL: string;
  bfcURL: string;
  bfcChangedFiles: CommitItem[];
  bicChangedFiles: CommitItem[];
  testCaseName: string;
  testFilePath: string;
  descriptionTxt: string;
};

export type RegressionCode = {
  regressionUuid: string;
  oldCode: string;
  newCode: string;
};
