export type ddResultItems = {
  info: ddInfoItems;
  steps: ddStepsItems[];
};

export type ddInfoItems = {
  projectFullName: string;
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
  testResult: string;
  testedHunks: string[];
  cProDDResults: number[];
  dProDDResults?: number[];
};
