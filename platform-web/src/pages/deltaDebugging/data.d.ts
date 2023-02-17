export type DdResultItems = {
  allHunkEntities: HunkEntityItems[];
  stepInfo: DdStepsItems[];
};

// exist at editor/data.d.ts
export type HunkEntityItems = {
  newPath: string;
  oldPath: string;
  beginA: number;
  beginB: number;
  endA: number;
  endB: number;
  type: string;
};

export type DdStepsItems = {
  stepNum: number;
  stepTestResult: string; // PASS | CE | FAIL
  cprob: number[];
  dprob?: number[];
  leftIdx2Test: number[]; // hunks left to test
  stepTestedInx: number[] | null; // hunk tested this step
  // ccHunks: HunkEntityItems[];
};

export interface RunDDInputParams {
  regression_uuid: string;
  revision_name: string; // bic | bfc
  start_step: number;
  userToken: string;
}

export interface RunDDByStepInputParams {
  regressionUuid: string;
  revisionName: string;
  startStep: number;
  endStep: number;
  cprob: number[];
  leftIdx2Test: number[];
  testedHunkIdx: number[];
  userToken: string;
}
