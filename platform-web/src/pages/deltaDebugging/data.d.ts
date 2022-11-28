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
}

export type DdStepsItems = {
  stepNum: number;
  stepTestResult: string; // PASS | CE | FAIL
  cprob: number[];
  dprob?: number[];
  cprobLeftIdx2Test: number[];  // hunks left to test
  cprobTestedInx: number[] | null; // hunk tested this step
  // ccHunks: HunkEntityItems[];
};

export interface RunDDInputParams {
  regression_uuid: string;
  revision_name: string; // bic | bfc
  start_step: number;
  end_step?: number;
  userToken: string;
  cPro?: number[];
  cProb_left_idx_to_test?: number[];
};
