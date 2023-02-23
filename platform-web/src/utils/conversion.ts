export function rateShow(num: number) {
  //0.25-0.75 0.5   0.75-1.25 1
  //1.25-1.75 1.5
  for (let i = 5; i >= 0; i -= 0.5) {
    if (Math.abs(i - num) <= 0.25) {
      return i;
    }
  }
  return 0;
}

export function str2number(str?: string) {
  if (str === undefined) return -1;
  const basic = 'A'.charCodeAt(0);
  let num = 0;
  for (let i = 0; i < str.length; ++i) {
    num += str.charCodeAt(i) - basic;
  }
  return num;
}

export function underline2camel(key: string) {
  return key.replace(/_([a-z])/g, (_, m) => m.toUpperCase());
}

export function camel2underline(key: string) {
  return key.replace(/([A-Z])/g, (_, m) => `_${m.toLowerCase()}`);
}

export const convData2Camel = <T, U>(params: T): U => {
  return Object.entries(params).reduce((acc, [key, value]) => {
    if (value !== undefined && value !== null) {
      // @ts-ignore
      acc[underline2camel(key)] = value;
    }
    return acc;
  }, {} as U);
};

export const convData2underline = <T, U>(params: T): U => {
  return Object.entries(params).reduce((acc, [key, value]) => {
    if (value !== undefined && value !== null) {
      // @ts-ignore
      acc[camel2underline(key)] = value;
    }
    return acc;
  }, {} as U);
};

export function str2arr(str?: string) {
  return str ? str.split(',') : undefined;
}

export function arr2str(arr?: any[]) {
  return Array.isArray(arr) ? arr.join(',') : undefined;
}

export function getLanguage(filePath: string) {
  return filePath.replace(/[^.]*\.([A-Za-z]+)$/, (_, p1) => p1);
}
