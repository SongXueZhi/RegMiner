import React, { useCallback, useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Comment,
  Button,
  Card,
  Descriptions,
  List,
  Menu,
  message,
  Popconfirm,
  Spin,
  Tag,
  Tooltip,
  Typography,
  Divider,
  Form,
  Modal,
} from 'antd';
import { AppstoreOutlined, DeleteOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
import DiffEditorTabs from './components/DiffEditorTabs';
import type { IRouteComponentProps } from 'umi';
import { useAccess, useModel } from 'umi';
import {
  getRegressionConsole,
  queryRegressionCode,
  queryRegressionDetail,
  getRegressionPath,
  regressionCheckout,
  postClearCache,
  getCommentList,
  deleteComment,
  addComment,
  getRetrievalCriticalChangeReviewList,
  deleteCriticalChangeReviewById,
  putCriticalChangeReviewById,
  queryRegressionMigrate,
  getRegressionBugTypes,
} from './service';
import type {
  BugTypeItems,
  CommentAPI,
  CommentListItems,
  CommitItem,
  DiffEditDetailItems,
  FeedbackList,
  FilePaneItem,
  HunkEntityItems,
} from './data';
import { parse } from 'query-string';
import TextArea from 'antd/lib/input/TextArea';
import BugType from './components/BugType';
import TagBugTypes from './components/TagBugType';

const { SubMenu } = Menu;

const testMethodList = [
  {
    key: 'testcase',
    tab: 'Test cases',
  },
  {
    key: 'features',
    tab: 'Features',
  },
];

interface IHistorySearch {
  regressionUuid: string;
  bic: string;
}

// function markMatch(
//   bic: CommitItem[],
//   bfc: CommitItem[],
// ): { bfcMatch: CommitItem[]; bicMatch: CommitItem[] } {
//   for (let i = 0; i < bfc.length; i++) {
//     if(bfc.includes(bic[i]))
//       bic[i].matchStatus = true;
//     }
//   }
// }

// // 轮询函数
// function useInterval(callback: any, delay: number | null | undefined) {
//   // useEffect(() => {
//   savedCallback.current = callback;
//   // });
//   // useEffect(() => {
//   function tick() {
//     savedCallback.current();
//   }
//   if (delay !== null) {
//     const id = setInterval(tick, delay);
//     return () => clearInterval(id);
//   }
//   console.log('end: ' + counter);
//   // }, [delay]);
// }

const EditorPage: React.FC<IRouteComponentProps> = ({ location }) => {
  const access = useAccess();
  const { initialState } = useModel('@@initialState');
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
  const [regressionBugTypes, setRegressionBugTypes] = useState<BugTypeItems[]>([]);
  const [openTagBugType, setOpenTagBugType] = useState<boolean>(false);
  const [BICisRunning, setBICIsRunning] = useState<boolean>(false);
  const [BFCisRunning, setBFCIsRunning] = useState<boolean>(false);
  const [BICFeedbackList, setBICFeedbackList] = useState<FeedbackList[]>([]);
  const [BFCFeedbackList, setBFCFeedbackList] = useState<FeedbackList[]>([]);
  const [BICCriticalChanges, setBICCriticalChanges] = useState<HunkEntityItems[]>([]);
  const [BFCCriticalChanges, setBFCCriticalChanges] = useState<HunkEntityItems[]>([]);
  const [loadingClearCache, setLoadingClearCache] = useState<boolean>(false);
  const [commentList, setCommentList] = useState<CommentAPI[]>([]);
  const [newCommentText, setNewCommentText] = useState<string>('');

  const getFile = async (params: {
    commit: string;
    bugId: string;
    filename: string;
    newPath: string;
    oldPath: string;
  }) => {
    if (params.commit === 'BFC') {
      return (
        (await queryRegressionCode({
          regression_uuid: HISTORY_SEARCH.regressionUuid,
          filename: params.filename,
          userToken: '123',
          new_path: params.newPath,
          old_path: params.oldPath,
          revisionFlag: 'bfc',
        })) ?? ''
      );
      // return bicFile;
    }
    if (params.commit === 'BIC') {
      return (
        (await queryRegressionCode({
          regression_uuid: HISTORY_SEARCH.regressionUuid,
          filename: params.filename,
          userToken: '123',
          new_path: params.newPath,
          old_path: params.oldPath,
          revisionFlag: 'bic',
        })) ?? ''
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
        resolve(true);
      }, ms);
    });
  }

  const handleMenuClick = useCallback(
    (
      commit,
      filename,
      oldPath,
      newPath,
      editList: DiffEditDetailItems[],
      CriticalChange: HunkEntityItems | undefined,
    ) => {
      const key = `${commit}-${filename}`;
      const project = projectFullName ?? '';
      // const [key, commit] = keyPath;
      // const [_, filename] = key.split(`${commit}-`);
      getFile({
        commit: commit,
        bugId: '',
        filename: filename,
        newPath: newPath,
        oldPath: oldPath,
      })
        .then((resp: any) => {
          wait(500);
          if (commit === 'BIC') {
            if (
              panesBIC.some((data) => {
                return data.key === key;
              })
            ) {
              const paneBIFIndex = panesBIC.findIndex((data) => {
                return data.key === key;
              });
              panesBIC.splice(paneBIFIndex, 1, {
                ...resp,
                key,
                editList,
                newPath,
                oldPath,
                CriticalChange,
                project,
              });
              setPanesBIC(panesBIC);
            } else {
              setPanesBIC(
                panesBIC.concat({
                  ...resp,
                  key,
                  editList,
                  newPath,
                  oldPath,
                  CriticalChange,
                  project,
                }),
              );
            }
          }
          if (commit === 'BFC') {
            if (
              panesBFC.some((data) => {
                return data.key === key;
              })
            ) {
              const paneBFCIndex = panesBFC.findIndex((data) => {
                return data.key === key;
              });
              panesBFC.splice(paneBFCIndex, 1, {
                ...resp,
                key,
                editList,
                newPath,
                oldPath,
                CriticalChange,
                project,
              });
              setPanesBFC(panesBFC);
            } else {
              setPanesBFC(
                panesBFC.concat({
                  ...resp,
                  key,
                  editList,
                  newPath,
                  oldPath,
                  CriticalChange,
                  project,
                }),
              );
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
    [panesBFC, panesBIC, projectFullName],
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
        handleMenuClick('BIC', resp.filename, resp.oldPath, resp.newPath, resp.editList, undefined);
      });
      bfcTestCaseList.map((resp) => {
        handleMenuClick('BFC', resp.filename, resp.oldPath, resp.newPath, resp.editList, undefined);
      });
    }
  };

  const handleSubmitFeedbacks = useCallback(() => {
    if (access.allUsersFoo) {
      BICFeedbackList.map((resp) => {
        if (resp.feedback === 'add' || resp.feedback === 'ground truth') {
          const targetCC = BICCriticalChanges.find(
            (d) =>
              (resp.hunkData.beginB <= d.beginB && resp.hunkData.endB >= d.beginB) ||
              (resp.hunkData.beginB >= d.beginB && resp.hunkData.beginB <= d.endB),
          );
          putCriticalChangeReviewById(
            {
              regression_uuid: HISTORY_SEARCH.regressionUuid,
              revision_name: resp.revision,
              account_name: initialState?.currentUser?.accountName,
              feedback: resp.feedback,
              review_id: targetCC?.reviewId,
            },
            resp.hunkData,
          );
        } else if (resp.feedback === 'reject') {
          const targetCC = BICCriticalChanges.find(
            (d) =>
              (resp.hunkData.beginB <= d.beginB && resp.hunkData.endB >= d.beginB) ||
              (resp.hunkData.beginB >= d.beginB && resp.hunkData.beginB <= d.endB),
          );
          if (targetCC) {
            deleteCriticalChangeReviewById({
              regression_uuid: HISTORY_SEARCH.regressionUuid,
              revision_name: resp.revision,
              review_id: targetCC.reviewId,
            });
          } else {
            message.error(
              `The reject feedback ${resp.fileName} does not include any critical change, auto withdrawed!`,
            );
          }
        } else {
          console.log('feedback type not right');
        }
      });
      BFCFeedbackList.map((resp) => {
        if (resp.feedback === 'add' || resp.feedback === 'ground truth') {
          const targetCC = BFCCriticalChanges.find(
            (d) =>
              (resp.hunkData.beginB <= d.beginB && resp.hunkData.endB >= d.beginB) ||
              (resp.hunkData.beginB >= d.beginB && resp.hunkData.beginB <= d.endB),
          );
          putCriticalChangeReviewById(
            {
              regression_uuid: HISTORY_SEARCH.regressionUuid,
              revision_name: resp.revision,
              account_name: initialState?.currentUser?.accountName,
              feedback: resp.feedback,
              review_id: targetCC?.reviewId,
            },
            resp.hunkData,
          );
        } else if (resp.feedback === 'reject') {
          const targetCC = BFCCriticalChanges.find(
            (d) =>
              (resp.hunkData.beginB <= d.beginB && resp.hunkData.endB >= d.beginB) ||
              (resp.hunkData.beginB >= d.beginB && resp.hunkData.beginB <= d.endB),
          );
          console.log(targetCC);
          if (targetCC) {
            deleteCriticalChangeReviewById({
              regression_uuid: HISTORY_SEARCH.regressionUuid,
              revision_name: resp.revision,
              review_id: targetCC.reviewId,
            });
          } else {
            message.error(
              `The reject feedback ${resp.fileName} does not include any critical change, auto withdrawed!`,
            );
          }
        } else {
          console.log('feedback type not right');
        }
      });
      setBFCFeedbackList([]);
      setBICFeedbackList([]);
      message.success('Submit successful');
      getRetrievalCriticalChangeReviewList({
        regression_uuid: HISTORY_SEARCH.regressionUuid,
        revision_name: 'bic',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          setBICCriticalChanges(resp.hunkEntityPlusList);
        }
      });
      getRetrievalCriticalChangeReviewList({
        regression_uuid: HISTORY_SEARCH.regressionUuid,
        revision_name: 'bfc',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          setBFCCriticalChanges(resp.hunkEntityPlusList);
        }
      });
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    BICFeedbackList,
    BFCFeedbackList,
    HISTORY_SEARCH.regressionUuid,
    BICCriticalChanges,
    BFCCriticalChanges,
  ]);

  const handleWithdrawFeedbacks = useCallback(
    (decorationKey, hunkData, revision) => {
      if (revision === 'bic') {
        const newList = BICFeedbackList.filter((resp) => resp.decorationKey !== decorationKey);
        setBICFeedbackList(newList);
      } else {
        const newList = BFCFeedbackList.filter((resp) => resp.decorationKey !== decorationKey);
        setBFCFeedbackList(newList);
      }
    },
    [BICFeedbackList, BFCFeedbackList],
  );

  const handleBICFeedbackList = useCallback((v: FeedbackList) => {
    // const newList = BICFeedbackList.splice(0, 0, v)
    setBICFeedbackList((value) => {
      return [v, ...value];
    });
  }, []);

  const handleBFCFeedbackList = useCallback((v: FeedbackList) => {
    // const newList = BFCFeedbackList.splice(0, 0, v);
    setBFCFeedbackList((value) => {
      return [v, ...value];
    });
  }, []);

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

  const handleClearCacheClick = async () => {
    if (access.allUsersFoo) {
      setLoadingClearCache(true);
      await postClearCache({
        userToken: '123',
        regressionUuid: HISTORY_SEARCH.regressionUuid,
        projectFullName: projectFullName ?? '',
      })
        .then(() => {
          setLoadingClearCache(false);
        })
        .catch(() => {
          message.error('Failed to clear cache');
          setLoadingClearCache(false);
        });
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
  };

  const handleDeleteComment = useCallback(
    (items: CommentListItems) => {
      if (access.onlyAdminFoo || items.accountName === initialState?.currentUser?.accountName) {
        deleteComment({
          regression_uuid: HISTORY_SEARCH.regressionUuid,
          account_name: items.accountName,
          comment_id: items.commentId,
        }).then(() => {
          getCommentList({ regression_uuid: HISTORY_SEARCH.regressionUuid }).then((resp) => {
            if (resp !== null && resp !== undefined) {
              let currComments: CommentAPI[] = [];
              currComments = resp.map((data) => {
                return {
                  actions: [
                    <Tooltip key="comment-delete-btn" title="Delete">
                      <span onClick={() => handleDeleteComment(data)}>
                        <DeleteOutlined />
                      </span>
                    </Tooltip>,
                  ],
                  author: data.accountName,
                  avatar: 'https://joeschmoe.io/api/v1/random',
                  content: <p>{data.context}</p>,
                  datetime: (
                    <Tooltip
                      title={`${data.createTime.substring(0, 10)} ${data.createTime.substring(
                        11,
                        19,
                      )}`}
                    >
                      <span>{`${data.createTime.substring(0, 10)} ${data.createTime.substring(
                        11,
                        19,
                      )}`}</span>
                    </Tooltip>
                  ),
                };
              });
              setCommentList(currComments);
            }
          });
        });
      } else {
        message.error('Sorry, you have no right to do that. Please login or use another account!');
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [HISTORY_SEARCH.regressionUuid],
  );

  const handleSubmitComment = useCallback(() => {
    if (access.allUsersFoo) {
      addComment({
        regression_uuid: HISTORY_SEARCH.regressionUuid,
        account_name: initialState?.currentUser?.accountName ?? undefined,
        context: newCommentText,
      }).then(() => {
        setNewCommentText('');
        getCommentList({ regression_uuid: HISTORY_SEARCH.regressionUuid }).then((resp) => {
          if (resp !== null && resp !== undefined) {
            let currComments: CommentAPI[] = [];
            currComments = resp.map((data) => {
              return {
                actions: [
                  <Tooltip key="comment-delete-btn" title="Delete">
                    <span onClick={() => handleDeleteComment(data)}>
                      <DeleteOutlined />
                    </span>
                  </Tooltip>,
                ],
                author: data.accountName,
                avatar: 'https://joeschmoe.io/api/v1/random',
                content: <p>{data.context}</p>,
                datetime: (
                  <Tooltip
                    title={`${data.createTime.substring(0, 10)} ${data.createTime.substring(
                      11,
                      19,
                    )}`}
                  >
                    <span>{`${data.createTime.substring(0, 10)} ${data.createTime.substring(
                      11,
                      19,
                    )}`}</span>
                  </Tooltip>
                ),
              };
            });
            setCommentList(currComments);
          }
        });
      });
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [HISTORY_SEARCH.regressionUuid, handleDeleteComment, newCommentText]);

  useEffect(() => {
    if (
      HISTORY_SEARCH.bic !== undefined &&
      HISTORY_SEARCH.bic !== '' &&
      HISTORY_SEARCH.bic !== null
    ) {
      regressionCheckout({ regression_uuid: HISTORY_SEARCH.regressionUuid, userToken: '123' }).then(
        () => {
          queryRegressionMigrate({
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
    } else {
      regressionCheckout({ regression_uuid: HISTORY_SEARCH.regressionUuid, userToken: '123' }).then(
        () => {
          queryRegressionDetail({
            regression_uuid: HISTORY_SEARCH.regressionUuid,
            userToken: '123',
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
    }
    getRetrievalCriticalChangeReviewList({
      regression_uuid: HISTORY_SEARCH.regressionUuid,
      revision_name: 'bic',
    }).then((resp) => {
      if (resp !== null && resp !== undefined) {
        setBICCriticalChanges(resp.hunkEntityPlusList);
      }
    });
    getRegressionBugTypes({ regression_uuid: HISTORY_SEARCH.regressionUuid }).then((data) => {
      if (data !== null && data !== undefined) {
        setRegressionBugTypes(data);
      }
    });
    getRetrievalCriticalChangeReviewList({
      regression_uuid: HISTORY_SEARCH.regressionUuid,
      revision_name: 'bfc',
    }).then((resp) => {
      if (resp !== null && resp !== undefined) {
        setBFCCriticalChanges(resp.hunkEntityPlusList);
      }
    });
    getCommentList({ regression_uuid: HISTORY_SEARCH.regressionUuid }).then((resp) => {
      if (resp !== null && resp !== undefined) {
        let currComments: CommentAPI[] = [];
        currComments = resp.map((data) => {
          return {
            actions: [
              <Tooltip key="comment-delete-btn" title="Delete">
                <span onClick={() => handleDeleteComment(data)}>
                  <DeleteOutlined />
                </span>
              </Tooltip>,
            ],
            author: data.accountName,
            avatar: 'https://joeschmoe.io/api/v1/random',
            content: <p>{data.context}</p>,
            datetime: (
              <Tooltip
                title={`${data.createTime.substring(0, 10)} ${data.createTime.substring(11, 19)}`}
              >
                <span>
                  {`${data.createTime.substring(0, 10)} ${data.createTime.substring(11, 19)}`}
                </span>
              </Tooltip>
            ),
          };
        });
        setCommentList(currComments);
      }
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [HISTORY_SEARCH.regressionUuid]);

  return (
    <>
      <Spin size="large" spinning={isLoading} tip={'Loading...'}>
        <PageContainer
          onBack={() => window.history.back()}
          // fixedHeader
          header={{
            title: 'Regression verfication',
            subTitle: (
              <div>
                <span style={{ marginRight: 20 }}>
                  <Typography.Text>
                    Regression UUID: {HISTORY_SEARCH.regressionUuid}
                  </Typography.Text>
                </span>
                {HISTORY_SEARCH.bic ? (
                  <span style={{ marginRight: 20 }}>
                    <Typography.Text>bic ID: {HISTORY_SEARCH.bic}</Typography.Text>
                  </span>
                ) : null}
              </div>
            ),
            footer: (
              <div style={{ display: 'inline-flex', alignItems: 'center' }}>
                <Descriptions column={3} style={{ flex: 1 }}>
                  <Descriptions.Item label={'Project'} labelStyle={{ fontWeight: 'bold' }}>
                    <Typography.Text keyboard strong>
                      {projectFullName}
                    </Typography.Text>
                  </Descriptions.Item>
                  <Descriptions.Item
                    label={'Bug Inducing Commit'}
                    labelStyle={{ fontWeight: 'bold' }}
                  >
                    <Typography.Link keyboard href={BICURL} target="_blank">
                      {BIC?.slice(0, 8)}...
                    </Typography.Link>
                    <br />
                  </Descriptions.Item>
                  <Descriptions.Item
                    label={'Bug Fixing Commit'}
                    labelStyle={{ fontWeight: 'bold' }}
                  >
                    <Typography.Link keyboard href={BFCURL} target="_blank">
                      {BFC?.slice(0, 8)}...
                    </Typography.Link>
                    <br />
                  </Descriptions.Item>
                  <Descriptions.Item
                    label={'Regression description'}
                    labelStyle={{ fontWeight: 'bold' }}
                  >
                    <Typography.Text>{regressionDescription}</Typography.Text>
                  </Descriptions.Item>
                  <Descriptions.Item label={'Bug Types'} labelStyle={{ fontWeight: 'bold' }}>
                    {regressionBugTypes.map((resp) => {
                      return (
                        <BugType
                          bugTypeId={resp.bugTypeId}
                          bugTypeName={resp.bugTypeName}
                          agreeCount={resp.agreeCount}
                          disagreeCount={resp.disagreeCount}
                          regressionUuid={HISTORY_SEARCH.regressionUuid}
                          onUpdateData={() => {
                            getRegressionBugTypes({
                              regression_uuid: HISTORY_SEARCH.regressionUuid,
                            }).then((data) => {
                              if (data !== null && data !== undefined) {
                                setRegressionBugTypes(data);
                              }
                            });
                          }}
                        />
                      );
                    })}
                    <Tag
                      style={{ background: '#fff', borderStyle: 'dashed' }}
                      onClick={() => {
                        if (access.allUsersFoo) {
                          setOpenTagBugType(true);
                        } else {
                          message.error(
                            'Sorry, you have no right to do that. Please login or use another account!',
                          );
                        }
                      }}
                    >
                      <PlusOutlined /> New Tag
                    </Tag>
                  </Descriptions.Item>
                </Descriptions>
                <Button onClick={handleClearCacheClick} loading={loadingClearCache}>
                  Clear Cache
                </Button>
              </div>
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
              <Card
                title="Changed files"
                style={{ marginBottom: 10 }}
                bordered={false}
                bodyStyle={{ padding: 0 }}
              >
                <Menu
                  title="Changed Files"
                  style={{ width: 286, maxHeight: '70vh', overflow: 'auto' }}
                  defaultOpenKeys={['BIC', 'BFC']}
                  mode="inline"
                >
                  {/* 优先显示test，在有match时显示check然后tooltip上加‘recomend to check’。
                （migrate迁移）*/}
                  <SubMenu key="BIC" icon={<AppstoreOutlined />} title="Bug Inducing Commit">
                    {listBIC.map(({ filename, match, oldPath, newPath, type, editList }) => {
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
                          onClick={() =>
                            handleMenuClick('BIC', filename, oldPath, newPath, editList, undefined)
                          }
                        >
                          {mark}
                          {filename}
                        </Menu.Item>
                      );
                    })}
                  </SubMenu>
                  <SubMenu key="BFC" icon={<AppstoreOutlined />} title="Bug Fixing Commit">
                    {listBFC.map(({ filename, match, oldPath, newPath, type, editList }) => {
                      let mark: any;
                      if (match === 1 && type !== null && type !== undefined) {
                        mark = null;
                      } else if (type !== null && type !== undefined) {
                        if (
                          type.toLowerCase() === 'test suite' ||
                          type.toLowerCase() === 'test_suite'
                        ) {
                          mark = null;
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
                          onClick={() =>
                            handleMenuClick('BFC', filename, oldPath, newPath, editList, undefined)
                          }
                        >
                          {mark}
                          {filename}
                        </Menu.Item>
                      );
                    })}
                  </SubMenu>
                </Menu>
              </Card>
              <Card
                title="Critical changes review"
                bordered={false}
                bodyStyle={{ padding: 0 }}
                style={{ marginBottom: 10 }}
              >
                <Menu
                  style={{ width: 286, maxHeight: '70vh', overflow: 'auto' }}
                  defaultOpenKeys={['BIC-Critical-Changes', 'BFC-Critical-Changes']}
                  mode="inline"
                >
                  <SubMenu
                    key="BIC-Critical-Changes"
                    icon={<AppstoreOutlined />}
                    title="Bug Inducing Commit"
                  >
                    {BICCriticalChanges.map((CCData) => {
                      const BICFileItems = listBIC.find(
                        (resp) =>
                          resp.newPath === CCData.newPath && resp.oldPath === CCData.oldPath,
                      );
                      if (BICFileItems) {
                        return (
                          <Menu.Item
                            key={`BIC-${BICFileItems.filename}-${CCData.reviewId}`}
                            onClick={() => {
                              handleMenuClick(
                                'BIC',
                                BICFileItems.filename,
                                BICFileItems.oldPath,
                                BICFileItems.newPath,
                                BICFileItems.editList,
                                CCData,
                              );
                            }}
                          >
                            <Tooltip title={`Line: ${CCData.beginB} ~ ${CCData.endB}`}>
                              {BICFileItems.filename}
                            </Tooltip>
                          </Menu.Item>
                        );
                      } else {
                        return null;
                      }
                    })}
                  </SubMenu>
                  <SubMenu
                    key="BFC-Critical-Changes"
                    icon={<AppstoreOutlined />}
                    title="Bug Fixing Commit"
                  >
                    {BFCCriticalChanges.map((CCData) => {
                      const BFCFileItems = listBFC.find(
                        (resp) =>
                          resp.newPath === CCData.newPath && resp.oldPath === CCData.oldPath,
                      );
                      if (BFCFileItems) {
                        return (
                          <Menu.Item
                            key={`BFC-${BFCFileItems.filename}-${CCData.reviewId}`}
                            onClick={() => {
                              handleMenuClick(
                                'BFC',
                                BFCFileItems.filename,
                                BFCFileItems.oldPath,
                                BFCFileItems.newPath,
                                BFCFileItems.editList,
                                CCData,
                              );
                            }}
                          >
                            <Tooltip title={`Line: ${CCData.beginB} ~ ${CCData.endB}`}>
                              {BFCFileItems.filename}
                            </Tooltip>
                          </Menu.Item>
                        );
                      } else {
                        return null;
                      }
                    })}
                  </SubMenu>
                </Menu>
              </Card>
              <Card
                title="Feedbacks"
                bordered={false}
                bodyStyle={{ padding: 0 }}
                extra={
                  <Button
                    type="primary"
                    shape="round"
                    icon={<UploadOutlined />}
                    onClick={handleSubmitFeedbacks}
                    disabled={
                      BICFeedbackList.length === 0 && BFCFeedbackList.length === 0 ? true : false
                    }
                  >
                    Submit
                  </Button>
                }
              >
                <Menu
                  style={{ width: 286, maxHeight: '70vh', overflow: 'auto' }}
                  defaultOpenKeys={['BIC-feedback', 'BFC-feedback']}
                  mode="inline"
                >
                  <SubMenu
                    key="BIC-feedback"
                    icon={<AppstoreOutlined />}
                    title="Bug Inducing Commit"
                  >
                    {BICFeedbackList?.map(
                      ({ decorationKey, fileName, feedback, hunkData, revision }, index) => {
                        return (
                          <Popconfirm
                            key={`BIC-popconfirm-${index}`}
                            title={
                              <>
                                <div>Feedback type: {feedback}</div>
                                <div>
                                  Feedback line: {hunkData.beginB} ~ {hunkData.endB}
                                </div>
                              </>
                            }
                            placement="top"
                            onCancel={() =>
                              handleWithdrawFeedbacks(decorationKey, hunkData, revision)
                            }
                            okText="OK"
                            cancelText="Withdraw"
                          >
                            <Menu.Item key={`BIC-${decorationKey}`}>
                              <Tag color="processing">{feedback}</Tag>
                              {fileName}
                            </Menu.Item>
                          </Popconfirm>
                        );
                      },
                    )}
                  </SubMenu>
                  <SubMenu key="BFC-feedback" icon={<AppstoreOutlined />} title="Bug Fixing Commit">
                    {BFCFeedbackList?.map(
                      ({ decorationKey, fileName, feedback, hunkData, revision }, index) => {
                        return (
                          <Popconfirm
                            key={`BIC-popconfirm-${index}`}
                            title={
                              <>
                                <div>Feedback type: {feedback}</div>
                                <div>
                                  Feedback line: {hunkData.beginB} ~ {hunkData.endB}
                                </div>
                              </>
                            }
                            placement="top"
                            onCancel={() =>
                              handleWithdrawFeedbacks(decorationKey, hunkData, revision)
                            }
                            okText="OK"
                            cancelText="Withdraw"
                          >
                            <Menu.Item key={`BFC-${decorationKey}`}>
                              <Tag color="processing">{feedback}</Tag>
                              {fileName}
                            </Menu.Item>
                          </Popconfirm>
                        );
                      },
                    )}
                  </SubMenu>
                </Menu>
              </Card>
            </div>
            {activeBICKey !== undefined && activeBICKey !== '' ? (
              <DiffEditorTabs
                commit="BIC"
                regressionUuid={HISTORY_SEARCH.regressionUuid}
                activeKey={activeBICKey}
                onActiveKey={setActiveBICKey}
                panes={panesBIC}
                onPanesChange={setPanesBIC}
                oldVersionText="work"
                newVersionText="bug introduce"
                onRunCode={handleBICRunClick}
                isRunning={BICisRunning}
                consoleString={BICConsoleResult}
                onFeedbackList={handleBICFeedbackList}
                onRevertCode={handleMenuClick}
              />
            ) : null}
            {activeBFCKey !== undefined && activeBFCKey !== '' ? (
              <DiffEditorTabs
                commit="BFC"
                regressionUuid={HISTORY_SEARCH.regressionUuid}
                activeKey={activeBFCKey}
                onActiveKey={setActiveBFCKey}
                panes={panesBFC}
                onPanesChange={setPanesBFC}
                oldVersionText="buggy"
                newVersionText="bug fix"
                onRunCode={handleBFCRunClick}
                isRunning={BFCisRunning}
                consoleString={BFCConsoleResult}
                onFeedbackList={handleBFCFeedbackList}
                onRevertCode={handleMenuClick}
              />
            ) : null}
          </div>
          <Card style={{ marginTop: '10px' }} title={`Regression Comments`}>
            <List
              className="comment-list"
              itemLayout="horizontal"
              dataSource={commentList}
              renderItem={(item) => (
                <li>
                  <Comment
                    actions={item.actions}
                    author={item.author}
                    avatar={item.avatar}
                    content={item.content}
                    datetime={item.datetime}
                  />
                  <Divider />
                </li>
              )}
            />
            <Form.Item>
              <TextArea
                rows={2}
                onChange={(value) => {
                  setNewCommentText(value.currentTarget.value);
                }}
                value={newCommentText}
                allowClear
              />
            </Form.Item>
            <Form.Item>
              <Button htmlType="submit" onClick={handleSubmitComment} type="primary">
                Add Comment
              </Button>
            </Form.Item>
          </Card>
        </PageContainer>
        {/* </Spin> */}
        <Modal
          title={`Regression: ${HISTORY_SEARCH.regressionUuid}`}
          open={openTagBugType}
          onCancel={() => {
            setOpenTagBugType(false);
            getRegressionBugTypes({
              regression_uuid: HISTORY_SEARCH.regressionUuid,
            }).then((data) => {
              if (data !== null && data !== undefined) {
                setRegressionBugTypes(data);
              }
            });
          }}
          footer={null}
        >
          <TagBugTypes regressionUuid={HISTORY_SEARCH.regressionUuid} />
        </Modal>
      </Spin>
    </>
  );
};

export default EditorPage;
