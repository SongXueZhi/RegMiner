export type ddResultItems = {
  info: ddInfoItems;
  steps: ddStepsItems[];
};

export type ddInfoItems = {
  regressionUuid: string;
  revision: string;
  filePath: string;
  allHunks: hunkDetailItems[];
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
