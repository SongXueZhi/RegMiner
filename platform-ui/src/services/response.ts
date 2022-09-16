import { message } from 'antd';

// eslint-disable-next-line consistent-return
export const handleResponse = ({ code, msg, data }: API.RegResponse<any>) => {
  if (code === 200) {
    return data;
  }
  message.error(msg);
};
