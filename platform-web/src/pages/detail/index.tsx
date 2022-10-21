import React, { useCallback, useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Button, Card, Descriptions, Menu, Radio, Spin, Tag, Tooltip, Typography } from 'antd';
import { AppstoreOutlined } from '@ant-design/icons';
import DiffEditorTabs from './components/DiffEditorTabs';
import type { IRouteComponentProps } from 'umi';
import {
  getRegressionConsole,
  queryRegressionCode,
  queryRegressionDetail,
  getRegressionPath,
  regressionCheckout,
} from './service';
import type { CommitItem } from './data';
import { parse } from 'query-string';

const { SubMenu } = Menu;

const testMethodList = [
  {
    key: 'testcase',
    tab: 'test cases',
  },
  {
    key: 'features',
    tab: 'features',
  },
];

interface IHistorySearch {
  regressionUuid: string;
  bic: string;
}

export type CommitFile = {
  newPath: string;
  oldPath: string;
  newCode: string;
  oldCode: string;
};

export interface FilePaneItem extends CommitFile {
  key: string;
}

const EditorPage: React.FC<IRouteComponentProps> = ({ location }) => {
  const HISTORY_SEARCH = parse(location.search) as unknown as IHistorySearch;
  // const savedCallback = useRef<any>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [testCaseName, setTestCaseName] = useState<string>();
  const [testTabKey, setTestTabKey] = useState('testcase');
  const [testFilePath, setTestFilePath] = useState<string>();
  const [activeBICKey, setActiveBICKey] = useState<string>();
  const [activeBFCKey, setActiveBFCKey] = useState<string>();
  const [BICConsoleResult, setBICConsoleResult] = useState<string>();
  const [BFCConsoleResult, setBFCConsoleResult] = useState<string>();
  const [panesBIC, setPanesBIC] = useState<FilePaneItem[]>([]);
  const [panesBFC, setPanesBFC] = useState<FilePaneItem[]>([]);
  const [listBIC, setListBIC] = useState<CommitItem[]>([]);
  const [listBFC, setListBFC] = useState<CommitItem[]>([]);
  const [projectFullName, setProjectFullName] = useState<string>();
  const [BIC, setBIC] = useState<string>();
  const [BFC, setBFC] = useState<string>();
  const [BICURL, setBICURL] = useState<string>();
  const [BFCURL, setBFCURL] = useState<string>();
  const [regressionDescription, setRegressionDescription] = useState<string>();
  const [BICisRunning, setBICIsRunning] = useState<boolean>(false);
  const [BFCisRunning, setBFCIsRunning] = useState<boolean>(false);

  const getFile = async (params: {
    commit: string;
    repoUuid: string;
    bugId: string;
    filename: string;
    newPath: string;
    oldPath: string;
  }) => {
    if (params.commit === 'BFC') {
      return (
        queryRegressionCode({
          regression_uuid: HISTORY_SEARCH.regressionUuid,
          filename: params.filename,
          userToken: '123',
          new_path: params.newPath,
          old_path: params.oldPath,
          revisionFlag: 'bfc',
        }) ?? ''
      );
      // return bicFile;
    }
    if (params.commit === 'BIC') {
      return (
        queryRegressionCode({
          regression_uuid: HISTORY_SEARCH.regressionUuid,
          filename: params.filename,
          userToken: '123',
          new_path: params.newPath,
          old_path: params.oldPath,
          revisionFlag: 'bic',
        }) ?? ''
        // return bfcFile;
      );
    }
    return {};
  };

  const getConsoleResult = async (params: {
    regression_uuid: string;
    revisionFlag: string; // work | bic | buggy | bfc
    userToken: string;
  }) => {
    if (params.revisionFlag === 'work') {
      const path = await getRegressionPath({
        regression_uuid: params.regression_uuid,
        revisionFlag: params.revisionFlag,
        userToken: '123',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          return resp;
        } else {
          return null;
        }
      });
      if (path !== null && path !== undefined) {
        setBICIsRunning(true);
        while (true) {
          const data = await getRegressionConsole({ path: path });
          await wait(500);
          setBICConsoleResult(data ?? '');
          if (data && data.includes('REGMINER-TEST-END')) {
            break;
          }
        }
        setBICIsRunning(false);
      }
    }
    if (params.revisionFlag === 'bug introduce') {
      const path = await getRegressionPath({
        regression_uuid: params.regression_uuid,
        revisionFlag: 'bic',
        userToken: '123',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          return resp;
        } else {
          return null;
        }
      });
      if (path !== null && path !== undefined) {
        setBICIsRunning(true);
        while (true) {
          const data = await getRegressionConsole({ path: path });
          await wait(500);
          setBICConsoleResult(data ?? '');
          if (data && data.includes('REGMINER-TEST-END')) {
            break;
          }
        }
        setBICIsRunning(false);
      }
    }

    if (params.revisionFlag === 'buggy') {
      await wait(500);
      const path = await getRegressionPath({
        regression_uuid: params.regression_uuid,
        revisionFlag: params.revisionFlag,
        userToken: '123',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          return resp;
        } else {
          return null;
        }
      });
      if (path !== null && path !== undefined) {
        setBFCIsRunning(true);
        while (true) {
          await wait(1000);
          const data = await getRegressionConsole({ path: path });
          setBFCConsoleResult(data ?? '');
          if (data && data.includes('REGMINER-TEST-END')) {
            break;
          }
        }
        setBFCIsRunning(false);
      }
    }
    if (params.revisionFlag === 'bug fix') {
      await wait(500);
      const path = await getRegressionPath({
        regression_uuid: params.regression_uuid,
        revisionFlag: 'bfc',
        userToken: '123',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          return resp;
        } else {
          return null;
        }
      });
      if (path !== null && path !== undefined) {
        setBFCIsRunning(true);
        while (true) {
          await wait(1000);
          const data = await getRegressionConsole({ path: path });
          setBFCConsoleResult(data ?? '');
          if (data && data.includes('REGMINER-TEST-END')) {
            break;
          }
        }
        setBFCIsRunning(false);
      }
    }
  };

  // 计时器
  function wait(ms: number) {
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('running');
        resolve(true);
      }, ms);
    });
  }

  const handleMenuClick = useCallback(
    (commit, filename, oldPath, newPath) => {
      const key = `${commit}-${filename}`;
      // const [key, commit] = keyPath;
      // const [_, filename] = key.split(`${commit}-`);
      getFile({
        commit: commit,
        repoUuid: '',
        bugId: '',
        filename: filename,
        newPath: newPath,
        oldPath: oldPath,
      })
        .then((resp: any) => {
          if (commit === 'BIC') {
            if (
              panesBIC.some((data) => {
                return data.key === key;
              })
            ) {
              setPanesBIC(panesBIC);
            } else {
              setPanesBIC(panesBIC.concat({ ...resp, key }));
            }
          }
          if (commit === 'BFC') {
            if (
              panesBFC.some((data) => {
                return data.key === key;
              })
            ) {
              setPanesBFC(panesBFC);
            } else {
              setPanesBFC(panesBFC.concat({ ...resp, key }));
            }
          }
        })
        .then(() => {
          if (commit === 'BIC') {
            setActiveBICKey(key);
          }
          if (commit === 'BFC') {
            setActiveBFCKey(key);
          }
        });
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [panesBFC, panesBIC],
  );

  const handleBICRunClick = useCallback(
    async (content, version) => {
      setBICConsoleResult('');
      const consoleResult = getConsoleResult({
        regression_uuid: HISTORY_SEARCH.regressionUuid,
        revisionFlag: version,
        userToken: '123',
      }).then((resp) => resp);
      return consoleResult;
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [getConsoleResult],
  );

  const handleBFCRunClick = useCallback(
    async (content, version) => {
      setBFCConsoleResult('');
      const consoleResult = getConsoleResult({
        regression_uuid: HISTORY_SEARCH.regressionUuid,
        revisionFlag: version,
        userToken: '123',
      }).then((resp) => resp);
      return consoleResult;
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [getConsoleResult],
  );

  const onTestTabChange = (key: React.SetStateAction<string>) => {
    setTestTabKey(key);
  };

  const handleTestTabClick = () => {
    if (testFilePath === 'NULL') {
      return console.log('no URL');
    } else {
      const bicTestCaseList = listBIC.filter((data) => {
        if (data.newPath === testFilePath && data.type === 'TEST_SUITE') {
          return data;
        } else {
          return null;
        }
      });
      const bfcTestCaseList = listBFC.filter((data) => {
        if (data.newPath === testFilePath && data.type === 'TEST_SUITE') {
          return data;
        } else {
          return null;
        }
      });
      bicTestCaseList.map((resp) => {
        handleMenuClick('BIC', resp.filename, resp.oldPath, resp.newPath);
      });
      bfcTestCaseList.map((resp) => {
        handleMenuClick('BFC', resp.filename, resp.oldPath, resp.newPath);
      });
    }
  };

  const contentListNoTitle = {
    testcase: (
      <Button
        disabled={testFilePath === 'NULL' ? true : false}
        onClick={handleTestTabClick}
        type="primary"
      >
        {testCaseName}
      </Button>
    ),
    features: <Typography.Text strong>N.A.</Typography.Text>,
  };

  useEffect(() => {
    regressionCheckout({ regression_uuid: HISTORY_SEARCH.regressionUuid, userToken: '123' }).then(
      () => {
        queryRegressionDetail({
          regression_uuid: HISTORY_SEARCH.regressionUuid,
          userToken: '123',
          bic: HISTORY_SEARCH.bic,
        }).then((data) => {
          if (data !== null && data !== undefined) {
            setListBFC(data.bfcChangedFiles);
            setListBIC(data.bicChangedFiles);
            setBFC(data.bfc);
            setBIC(data.bic);
            setBFCURL(data.bfcURL);
            setBICURL(data.bicURL);
            setProjectFullName(data.projectFullName);
            setTestCaseName(data.testCaseName);
            setTestFilePath(data.testFilePath);
            setRegressionDescription(data.descriptionTxt);
          }
          setIsLoading(false);
        });
      },
    );
  }, [HISTORY_SEARCH.regressionUuid, HISTORY_SEARCH.bic]);

  return (
    <>
      <Spin size="large" spinning={isLoading} tip={'Loading...'}>
        <PageContainer
          onBack={() => window.history.back()}
          // fixedHeader
          header={{
            title: 'Progress',
            subTitle: (
              <Typography.Text>Regression UUID: {HISTORY_SEARCH.regressionUuid}</Typography.Text>
            ),
          }}
        >
          <div style={{ display: 'flex' }}>
            <div>
              <Card
                // bordered={false}
                style={{ marginBottom: 10, width: 286, overflow: 'auto' }}
                tabList={testMethodList}
                activeTabKey={testTabKey}
                onTabChange={(key) => {
                  onTestTabChange(key);
                }}
              >
                {contentListNoTitle[testTabKey]}
              </Card>
              <Card title="Changed files" bordered={false} bodyStyle={{ padding: 0 }}>
                <Menu
                  title="菜单"
                  // onClick={handleMenuClick}
                  style={{ width: 286, maxHeight: '70vh', overflow: 'auto' }}
                  defaultOpenKeys={['BIC', 'BFC']}
                  mode="inline"
                >
                  {/* 优先显示test，在有match时显示check然后tooltip上加‘recomend to check’。
                （migrate迁移）*/}
                  <SubMenu key="BIC" icon={<AppstoreOutlined />} title="Target Commit">
                    {listBIC.map(({ filename, match, oldPath, newPath, type }) => {
                      let mark: any;
                      if (match === 1 && type !== null && type !== undefined) {
                        mark = <Tag color="success">Migrate</Tag>;
                      } else if (type !== null && type !== undefined) {
                        if (
                          type.toLowerCase() === 'test suite' ||
                          type.toLowerCase() === 'test_suite'
                        ) {
                          mark = <Tag color="processing">Migrate</Tag>;
                        } else {
                          mark = <Tag color="processing">{type}</Tag>;
                        }
                      } else if (match === 1) {
                        mark = (
                          <Tooltip title="recommend to check">
                            <Tag color="warning">check</Tag>
                          </Tooltip>
                        );
                      }
                      return (
                        <Menu.Item
                          key={`BIC-${filename}`}
                          onClick={() => handleMenuClick('BIC', filename, oldPath, newPath)}
                        >
                          {mark}
                          {filename}
                        </Menu.Item>
                      );
                    })}
                  </SubMenu>
                  {/* <SubMenu key="BFC" icon={<AppstoreOutlined />} title="Bug Fixing Commit">
                    {listBFC.map(({ filename, match, oldPath, newPath, type }) => {
                      let mark: any;
                      if (match === 1 && type !== null && type !== undefined) {
                        mark = <Tag color="success">Migrate</Tag>;
                      } else if (type !== null && type !== undefined) {
                        if (
                          type.toLowerCase() === 'test suite' ||
                          type.toLowerCase() === 'test_suite'
                        ) {
                          mark = <Tag color="processing">Migrate</Tag>;
                        } else {
                          mark = <Tag color="processing">{type}</Tag>;
                        }
                      } else if (match === 1) {
                        mark = (
                          <Tooltip title="recommend to check">
                            <Tag color="warning">check</Tag>
                          </Tooltip>
                        );
                      }
                      return (
                        <Menu.Item
                          key={`BFC-${filename}`}
                          onClick={() => handleMenuClick('BFC', filename, oldPath, newPath)}
                        >
                          {mark}
                          {filename}
                        </Menu.Item>
                      );
                    })}
                  </SubMenu> */}
                </Menu>
              </Card>
            </div>
            {activeBICKey !== undefined && activeBICKey !== '' && (
              <DiffEditorTabs
                commit="BIC"
                activeKey={activeBICKey}
                onActiveKey={setActiveBICKey}
                panes={panesBIC}
                onPanesChange={setPanesBIC}
                oldVersionText="work"
                newVersionText="bug introduce"
                onRunCode={handleBICRunClick}
                isRunning={BICisRunning}
                consoleString={BICConsoleResult}
              />
            )}
            {activeBFCKey !== undefined && activeBFCKey !== '' && (
              <DiffEditorTabs
                commit="BFC"
                activeKey={activeBFCKey}
                onActiveKey={setActiveBFCKey}
                panes={panesBFC}
                onPanesChange={setPanesBFC}
                oldVersionText="buggy"
                newVersionText="bug fix"
                onRunCode={handleBFCRunClick}
                isRunning={BFCisRunning}
                consoleString={BFCConsoleResult}
              />
            )}
          </div>
        </PageContainer>
      </Spin>
    </>
  );
};

export default EditorPage;
