export const getDistanceDay = (time: number) => {
  const stime = new Date().getTime();
  console.log('stime', stime);
  const usedTime = stime - time * 1000; //两个时间戳相差的毫秒数
  const days = Math.floor(usedTime / (24 * 3600 * 1000));
  //计算出小时数
  const leave1 = usedTime % (24 * 3600 * 1000); //计算天数后剩余的毫秒数
  const hours =
    Math.floor(leave1 / (3600 * 1000)) < 10
      ? '0' + Math.floor(leave1 / (3600 * 1000))
      : Math.floor(leave1 / (3600 * 1000));
  //计算相差分钟数
  const leave2 = leave1 % (3600 * 1000); //计算小时数后剩余的毫秒数
  const minutes =
    Math.floor(leave2 / (60 * 1000)) < 10
      ? '0' + Math.floor(leave2 / (60 * 1000))
      : Math.floor(leave2 / (60 * 1000));
  // 计算出秒
  const leave3 = leave2 % (60 * 1000);
  const sconeds =
    Math.floor(leave3 / 1000) < 10 ? '0' + Math.floor(leave3 / 1000) : Math.floor(leave3 / 1000);
  const resTime = days + ' days ' + hours + ' hours ' + minutes + ' minutes ';
  return resTime;
};
