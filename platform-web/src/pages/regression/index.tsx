import {PlusOutlined} from '@ant-design/icons';
import {Button, Divider, Input, message, Select, Skeleton, Tag, Tooltip} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import CreateForm from './components/CreateForm';
import {addRegression, getAllBugTypes, queryRegressionList, removeRegression} from './service';
import {Link} from 'react-router-dom';
import {stringify} from 'query-string';
import './index.less';
import {useAccess} from 'umi';
import type {AllBugTypes} from './data';

/**
 * 添加节点
 *
 * @param fields
 */
const handleAdd = async (fields: API.RegressionItem) => {
  const hide = message.loading('Adding');
  try {
    await addRegression({...fields});
    hide();
    message.success('Successfully added!');
    return true;
  } catch (error) {
    hide();
    message.error('Failed to add. Please try again!');
    return false;
  }
};

/**
 * 更新节点
 *
 * @param fields
 */
// const handleUpdate = async (fields: FormValueType) => {
//   const hide = message.loading('正在配置');
//   try {
//     await updateRule({
//       name: fields.name,
//       desc: fields.desc,
//       key: fields.key,
//     });
//     hide();

//     message.success('配置成功');
//     return true;
//   } catch (error) {
//     hide();
//     message.error('配置失败请重试！');
//     return false;
//   }
// };

/**
 * 删除节点
 *
 * @param selectedRows
 */
const handleRemove = async (regressionUuid: string) => {
  const hide = message.loading('Deleting');
  try {
    await removeRegression({
      regressionUuid,
    });
    hide();
    message.success('Deleted successfully, about to refresh!');
    return true;
  } catch (error) {
    hide();
    message.error('Deletion failed, please try again!');
    return false;
  }
};

const pageSize = 20;

function withSkeleton(element: JSX.Element | string | number | number | undefined) {
  return (
    element ?? <Skeleton title={{width: '80px', style: {margin: 0}}} paragraph={false} active/>
  );
}

interface SearchParams extends Record<string, any> {
  order?: string;
  page?: number;
  ps?: number;
  regressionUuid?: string;
  projectFullName?: string;
  keyword?: string;
  bugTypeNames?: string[];
}

// interface IHistorySearch extends SearchParams {
//   regression_uuid?: string;
//   project_full_name?: string;
//   keyword?: string;
//   bug_types?: string[];
// }

const generateParams = (params: SearchParams) => {
  return {
    regression_uuid: params.regressionUuid,
    project_full_name: params.projectFullName,
    ksyword: params.keyword,
    bug_type_name: params.bugTypeNames,
    page: params.current,
    ps: params.pageSize,
  };
};

const RegressionListPage: React.FC<{}> = () => {
  const access = useAccess();
  // const HISTORY_SEARCH = parse(location.search) as IHistorySearch;
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [regressionUuidList, setRegressionUuidList] = useState<string[]>([]);
  const [allBugTypes, setAllBugTypes] = useState<AllBugTypes[]>([]);
  const actionRef = useRef<ActionType>();
  const columns: ProColumns<API.RegressionItem>[] = [
    {
      title: 'No.',
      dataIndex: 'index',
      width: 48,
      render: (_, record) => {
        return record.index + 1;
      },
      search: false,
      // formItemProps: { label: 'keyword' },
    },
    {
      title: 'Bug UUID',
      dataIndex: 'regressionUuid',
      width: 200,
      render: (_, {projectFullName, regressionUuid, id}) => {
        return withSkeleton(
          regressionUuid ? (
            // index <= 49 ? (
            <Link
              to={{
                pathname: '/editor',
                search: stringify({regressionUuid}),
              }}
            >
              <Tooltip title={regressionUuid}>
                {projectFullName?.split('/')[1] + '_' + `${id}`}
              </Tooltip>
            </Link>
          ) : (
            // ) : (
            //   regressionUuid
            // )
            '暂无数据'
          ),
        );
      },
      // renderFormItem: (_, { record, type }, form) => {
      //   if (type === 'form') {
      //     return null;
      //   }
      //   return (
      //     <Select
      //       showSearch
      //       mode="multiple"
      //       maxTagCount="responsive"
      //       placeholder={'Regression UUID'}
      //       options={[]}
      //     />
      //   );
      // },
      renderFormItem: (_, {type}, form) => {
        if (type === 'form') {
          return null;
        }
        return <Input placeholder="Regression UUID"/>;
      },
    },
    {
      title: 'keyword',
      dataIndex: 'keyword',
      hideInTable: true,
      search: false,
      render: (_, {regressionUuid, index}) => {
        return withSkeleton(regressionUuid ? (index <= 49 ? '' : '') : '暂无数据');
      },
    },
    {
      title: 'Project name',
      dataIndex: 'projectFullName',
      width: 200,
      search: false,
      renderText: (val: string) => `${val} `,
      // tip: '所属项目名称',
    },
    {
      title: 'Bug inducing commit',
      dataIndex: 'bic',
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => {
        return (
          <Tooltip placement="top" title={record.bic}>
            {record.bic?.slice(0, 8)}...
          </Tooltip>
        );
      },
    },
    {
      title: 'Work commit',
      dataIndex: 'work',
      // tip: 'a random work commit',
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => {
        return (
          <Tooltip placement="top" title={record.work}>
            {record.work?.slice(0, 12)}...
          </Tooltip>
        );
      },
    },
    {
      title: 'Bug fixing commit',
      dataIndex: 'bfc',
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => {
        return (
          <Tooltip placement="top" title={record.bfc}>
            {record.bfc?.slice(0, 8)}...
          </Tooltip>
        );
      },
    },
    {
      title: 'Buggy commit',
      dataIndex: 'buggy',
      tip: 'the parent of bug fixing commit',
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => {
        return (
          <Tooltip placement="top" title={record.buggy}>
            {record.buggy?.slice(0, 12)}...
          </Tooltip>
        );
      },
    },
    {
      title: 'Bug type',
      dataIndex: 'bugTypeNames',
      render: (_, record) => {
        return record.bugTypeNames
          ? record.bugTypeNames.map((resp) => {
            return <Tag color="purple">{resp}</Tag>;
          })
          : '无分类';
      },
      renderFormItem: (_, {type}, form) => {
        if (type === 'form') {
          return null;
        }
        return (
          <Select
            mode="multiple"
            allowClear
            style={{width: '100%'}}
            placeholder="Please select"
            options={allBugTypes.map((resp) => {
              return {value: resp.bugTypeName, label: resp.bugTypeName};
            })}
            // onChange={(v) => {
            //   form.setFieldValue({bugtype});
            // }}
          />
        );
      },
    },
    {
      title: 'Bug DataSource', // 你可以更改此标题
      // dataIndex: 'toolSelector',
      hideInTable: true,
      renderFormItem: (_, {type}, form) => {
        if (type === 'form') {
          return null;
        }
        return (
          <Select style={{width: '100%'}} placeholder="Select a DataSource">
            <Select.Option value="regminer">Regminer</Select.Option>
            <Select.Option value="bugbuilder">BugBuilder</Select.Option>
            <Select.Option value="bugminer">Bugminer</Select.Option>
          </Select>
        );
      },
    },
    {
      title: 'Test case',
      dataIndex: 'testcase',
      hideInForm: true,
      hideInSearch: true,
      ellipsis: true,
      renderText: (val: string) => `${val} `,
    },
    {
      title: 'regression status',
      dataIndex: 'regressionStatus',
      initialValue: 'all',
      width: 150,
      search: false,
      hideInTable: true,
      filters: true,
      onFilter: true,
      valueEnum: {
        0: {text: 'Not verified', status: 'Default'},
        1: {text: 'confirmed', status: 'Success'},
        2: {text: 'rejected', status: 'Error'},
        3: {text: 'undecided', status: 'Processing'},
      },
    },
    {
      title: 'actions',
      hideInForm: true,
      hideInTable: true,
      search: false,
      fixed: 'right',
      render: (_, {regressionUuid: regressionUuid}) => [
        <Divider type="vertical"/>,
        <Button
          type="primary"
          danger
          onClick={() => {
            handleRemove(regressionUuid).then(() => {
              actionRef.current?.reload();
            });
          }}
        >
          delete
        </Button>,
      ],
    },
  ];

  const queryList = async (
    params: SearchParams & {
      pageSize?: number | undefined;
      current?: number | undefined;
      keyword?: string | undefined;
    },
  ) => {
    const totalParams = {
      ...generateParams(params),
    };
    console.log(totalParams);
    const resp = await queryRegressionList({...totalParams});
    const regressionList: string[] = resp.data.map((d) => {
      return d.regressionUuid;
    });
    setRegressionUuidList(regressionList);
    if (resp === null || typeof resp === 'boolean')
      return {
        data: [],
        success: true,
        total: 0,
      };
    return {
      data: resp.data,
      success: true,
      total: resp.data.length,
    };
  };

  useEffect(() => {
    getAllBugTypes().then((data) => {
      if (data) {
        setAllBugTypes(data);
      }
    });
  }, []);
  return (
    <PageContainer
      header={{
        style: {width: '100%'},
        title: 'Bug Repository',
        // subTitle: (
        //   <Alert
        //     style={{ paddingLeft: '100px', paddingRight: '100px' }}
        //     type="info"
        //     message={
        //       <div style={{ color: 'red', fontSize: '20px', fontWeight: 'bold' }}>
        //         Note! Due to cloud server limitations, only the first 50 bugs on the list are
        //         available.
        //       </div>
        //     }
        //   />
        // ),
      }}
    >
      {/* <div className="RegMiner-tutorial-video" style={{ marginBottom: '20px' }}>
        <Row justify="space-around" align="middle">
          <Col>
            <iframe
              src="https://www.youtube.com/embed/QtqS8f2yApc"
              title="RegMiner Tutorial"
              width="1080"
              height="650"
              allow="autoplay"
              allowFullScreen
              allowTransparency
            />
          </Col>
        </Row>
      </div> */}
      <ProTable<API.RegressionItem>
        headerTitle="Bug List"
        actionRef={actionRef}
        rowKey="regressionUuid"
        search={{
          labelWidth: 'auto',
          defaultCollapsed: false,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            onClick={() => {
              if (access.allUsersFoo) {
                handleModalVisible(true);
              } else {
                message.error(
                  'Sorry, you have no right to do that. Please login or use another account!',
                );
              }
            }}
          >
            <PlusOutlined/> add
          </Button>,
        ]}
        // @ts-ignore
        request={queryList}
        columns={columns}
        pagination={{
          pageSize: pageSize,
        }}
      />
      <CreateForm onCancel={() => handleModalVisible(false)} modalVisible={createModalVisible}>
        <ProTable<API.RegressionItem, API.RegressionItem>
          onSubmit={async (value) => {
            const success = await handleAdd(value);
            if (success) {
              handleModalVisible(false);
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          rowKey="key"
          type="form"
          columns={columns}
        />
      </CreateForm>
    </PageContainer>
  );
};

export default RegressionListPage;
