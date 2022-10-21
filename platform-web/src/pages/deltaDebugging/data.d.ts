export type ddResultItems = {
  info: ddInfoItems;
  steps: ddStepsItems;
};

export type ddInfoItems = {
  regression: string;
  revision: string;
  filePath: string;
  allHunk: hunkDetailItems[];
};

export type hunkDetailItems = {
  hunkId: string;
  oldCode: string;
  newCode: string;
};

export type ddStepsItems = {
  stepNum: number;
  stepResult: string;
  testedHunks: string[];
  testResults: any;
};
