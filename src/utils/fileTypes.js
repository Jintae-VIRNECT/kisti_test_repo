const FILE_3D = {
  key: 'threed',
  ext: [
    'stl',
    'ply',
    'wrl',
    'wrz',
    'x3d',
    'mars',
    'obj',
    'mtl',
    'igs',
    'iges',
    'stp',
    '3ds',
    'sat',
    'ipt',
    'iam',
    'model',
    'session',
    'exp',
    'dlv3',
    'CATPart',
    'CATProduct',
    'sed',
    'dgn',
    'fbx',
    'ij',
    'x_b',
    'x_t',
    'neu',
    '3dm',
    'prt',
    'sldprt',
    'asm',
    'sldasm',
    'wmf',
    'ste',
    'step',
    'max',
    'ma',
    'dwg',
    'dwx',
    'dxf',
    'dws',
    'glb',
    'gltf',
    'obj',
    'dae',
  ],
  mime: [],
}
const FILE_ZIP = {
  key: 'zip',
  ext: [
    'zip',
    'zipx',
    'exe',
    'apk',
    'rar',
    '7z',
    'tar',
    'tgz',
    'bz2',
    'lzh',
    'iso',
    'gz',
    'xz',
    'alz​',
  ],
  mime: [],
}
const FILE_PDF = {
  key: 'pdf',
  ext: ['pdf'],
  mime: [
    /* ETC */
    'application/pdf',
  ],
}
const FILE_AUDIO = {
  key: 'audio',
  ext: ['wav', 'mp3', 'ogg', 'dct', 'flac', 'au', 'aiff', 'wma', 'aac'],
  mime: [
    /* AUDIO */
    'audio/midi',
    'audio/mpeg',
    'audio/webm',
    'audio/ogg',
    'audio/wav',
    'audio/wave',
    'audio/x-wav',
    'audio/x-pn-wav',
  ],
}
const FILE_VIDEO = {
  key: 'video',
  ext: [
    'avi',
    'mp4',
    'mpg',
    'mpeg',
    'mpe',
    'wmv',
    'asf',
    'swf',
    'flv',
    'rm',
    'mov',
    'dat',
    'mkv',
  ],
  mime: [
    /* VIDEO */
    'video/webm',
    'video/ogg',
  ],
}
const FILE_IMAGE = {
  key: 'image',
  ext: [
    'gif',
    'jpg',
    'jpeg',
    'png',
    'tif',
    'tiff',
    'bmp',
    'raw',
    'pic',
    'pict',
    'wmf',
    'emf',
  ],
  mime: [
    /* IMAGE */
    'image/gif',
    'image/png',
    'image/jpeg',
    'image/bmp',
    'image/webp',
    'image/svg+xml',
  ],
}
const FILE_DOC = {
  key: 'doc',
  ext: [
    'doc',
    'docx',
    'docm',
    'dot',
    'dotx',
    'dotm',
    'xls',
    'xlsx',
    'xlsm',
    'xlsb',
    'xlt',
    'xltx',
    'xltxm',
    'ppt',
    'pptx',
    'pptm',
    'pot',
    'potx',
    'potm',
    'pps',
    'ppsx',
    'ppsm',
    'ppa',
    'hwp',
    'hwt',
    'hwpx',
    'hwtx',
    'rtf',
    'odp',
    'odt',
    'ar',
    'psd',
    'txt',
    'csv',
  ],
  mime: [
    /* MS Office */
    'application/msword', // .doc, .dot
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // docx
    'application/vnd.openxmlformats-officedocument.wordprocessingml.template', // .dotx
    'application/vnd.ms-word.document.macroEnabled.12', // .docm
    'application/vnd.ms-word.template.macroEnabled.12', // .dotm
    'application/vnd.ms-excel', // .xls, .xlt, .xla
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
    'application/vnd.openxmlformats-officedocument.spreadsheetml.template', // .xltx
    'application/vnd.ms-excel.sheet.macroEnabled.12', // .xlsm
    'application/vnd.ms-excel.template.macroEnabled.12', // .xltm
    'application/vnd.ms-excel.addin.macroEnabled.12', // .xlam
    'application/vnd.ms-excel.sheet.binary.macroEnabled.12', // .xlsb
    'application/vnd.ms-powerpoint', // .ppt, .pot, .pps, .ppa
    'application/vnd.openxmlformats-officedocument.presentationml.presentation', // .pptx
    'application/vnd.openxmlformats-officedocument.presentationml.template', // .potx
    'application/vnd.openxmlformats-officedocument.presentationml.slideshow', // .ppsx
    'application/vnd.ms-powerpoint.addin.macroEnabled.12', // .ppam
    'application/vnd.ms-powerpoint.presentation.macroEnabled.12', // .pptm
    'application/vnd.ms-powerpoint.template.macroEnabled.12', // .potm
    'application/vnd.ms-powerpoint.slideshow.macroEnabled.12', // .ppsm
    /* HWP */
    'application/x-hwp',
    'application/haansofthwp',
    'application/vnd.hancom.hwp', // .hwp
    'application/x-hwt',
    'application/haansofthwt',
    'application/vnd.hancom.hwt', // hwt
    'application/vnd.hancom.hml',
    'application/haansofthml', // hml, .hwpml
    'application/vnd.hancom.hwpx', // .hwpx
  ],
}

//사파리 모바일 에서만 video, audio type을 제외하기 위해 사용
export const fileTypesExceptVideoRelation = () => {
  return [
    ...FILE_3D.ext.map(ext => `.${ext}`),
    ...FILE_PDF.ext.map(ext => `.${ext}`),
    ...FILE_ZIP.ext.map(ext => `.${ext}`),
    ...FILE_IMAGE.ext.map(ext => `.${ext}`),
    ...FILE_DOC.ext.map(ext => `.${ext}`),
  ].join(', ')
}

/**
 * get file type
 * @return {String} '3d', 'zip', 'pdf', 'audio', 'video', 'image', 'doc', 'file'
 * @param {String} name file name
 * @param {String} type mimeType
 */
export const checkFileType = ({ name = '', type = '' }) => {
  const nameExp = name.split('.')
  const extension = nameExp[nameExp.length - 1]
  const checkList = [
    FILE_3D,
    FILE_ZIP,
    FILE_PDF,
    FILE_AUDIO,
    FILE_VIDEO,
    FILE_IMAGE,
    FILE_DOC,
  ]
  for (let check of checkList) {
    if (check.ext.includes(extension) || check.mime.includes(type)) {
      return check.key
    }
  }
  return 'file'
}
