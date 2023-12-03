import {Button, Form, Input, message, Select, Tag, Typography} from 'antd';
import {useModel} from 'umi';
import {useEffect, useState} from 'react';
import {getAllBugTypes} from '@/pages/regression/service';
import type {AllBugTypes} from '@/pages/regression/data';
import {addBugTypeToRegression, createNewBugType, getRegressionBugTypes} from '../service';
import type {BugTypeItems} from '../data';

interface IProps {
  regressionUuid: string;
  //   onUpdateTag: ({ bugTypeId, bugTypeName }: { bugTypeId: number; bugTypeName: string }) => void;
}

const TagBugTypes: React.FC<IProps> = ({regressionUuid}) => {
  const {initialState} = useModel('@@initialState');
  const [update, setUpdate] = useState<boolean>(false);
  const [regressionBugTypes, setRegressionBugTypes] = useState<BugTypeItems[]>([]);
  const [allBugTypes, setAllBugTypes] = useState<AllBugTypes[]>([]);
  const [disable, setDisable] = useState<boolean>(true);
  const [selectedBugTypeId, setSelectedBugTypeId] = useState<number>(0);
  const [selectedBugTypeName, setSelectedBugTypeName] = useState<string>('');
  const [createdBugType, setCreatedBugType] = useState<string>('');

  useEffect(() => {
    getRegressionBugTypes({
      regression_uuid: regressionUuid,
    }).then((data) => {
      if (data !== null && data !== undefined) {
        setRegressionBugTypes(data);
      }
    });
    getAllBugTypes().then((data) => {
      if (data) {
        setAllBugTypes(data);
      }
    });
  }, [regressionUuid, update]);

  return (
    <>
      <Form layout="horizontal">
        <Form.Item label="Tagged Bug Types">
          {regressionBugTypes.length !== 0 ? (
            regressionBugTypes.map((resp) => {
              return <Tag color="purple">{resp.bugTypeName}</Tag>;
            })
          ) : (
            <Typography.Text type="danger" strong>
              None
            </Typography.Text>
          )}
        </Form.Item>
        <Form.Item label="All Bug Types">
          <Select
            allowClear
            style={{width: '90%'}}
            placeholder="Select one"
            onChange={(_, options) => {
              if (options !== undefined) {
                // options type can only be {value: number; label: string}
                //@ts-ignore
                setSelectedBugTypeId(options.value);
                //@ts-ignore
                setSelectedBugTypeName(options.label);
                setDisable(false);
                console.log(options);
              } else {
                setDisable(true);
              }
            }}
            options={allBugTypes.map((resp) => {
              return {value: resp.bugTypeId, label: resp.bugTypeName};
            })}
          />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            onClick={() => {
              console.log(selectedBugTypeId);
              console.log(selectedBugTypeName);
              addBugTypeToRegression({
                regressionUuid: regressionUuid,
                bugTypeId: selectedBugTypeId,
                bugTypeName: selectedBugTypeName,
                accountName: initialState?.currentUser?.accountName ?? '',
              }).then(() => {
                setUpdate(!update);
              });
            }}
            disabled={disable}
          >
            Tag The Bug Type
          </Button>
        </Form.Item>
        <Form.Item label="Create New Bug Type">
          <Input.Group>
            <Input
              allowClear
              placeholder="Create New Bug Type"
              style={{width: 'calc(100% - 110px)'}}
              onChange={(e) => {
                setCreatedBugType(e.target.value);
              }}
            />
            <Button
              type="primary"
              disabled={createdBugType === ''}
              onClick={() => {
                if (createdBugType !== '') {
                  createNewBugType({
                    bugTypeName: createdBugType,
                    accountName: initialState?.currentUser?.accountName,
                  }).then(() => {
                    setUpdate(!update);
                    message.success('New bug type created! You can select it now.');
                  });
                }
              }}
            >
              Create
            </Button>
          </Input.Group>
        </Form.Item>
      </Form>
    </>
  );
};

export default TagBugTypes;
