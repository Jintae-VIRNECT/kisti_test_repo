import { fileSizeFilter } from 'utils/file'

describe('fileSizeFilter', () => {
  it('50KB 이하는 0.1MB로 표기되어야 한다', () => {
    const in50KB = 50 * 1024
    const result50KB = fileSizeFilter(in50KB)

    const in30KB = 30 * 1024
    const result30KB = fileSizeFilter(in30KB)

    expect(result50KB).toEqual('0.1MB')
    expect(result30KB).toEqual('0.1MB')
  })

  it('50KB 초과는 MB단위로 표기되어야 한다', () => {
    const size = 1000 * 50 * 1024
    const result = fileSizeFilter(size)

    expect(result).toEqual('48.8MB')
  })
})
