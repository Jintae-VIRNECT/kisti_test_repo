module.exports = {
  preset: '@vue/cli-plugin-unit-jest',

  moduleFileExtensions: ['js', 'json', 'vue'],
  moduleDirectories: ['node_modules', 'modules'],
  // modulePaths: ['/src/components/modules'],

  modulePathIgnorePatterns: [],
  coveragePathIgnorePatterns: [
    'node_modules',
    '<rootDir>/record/',
    '<rootDir>/vscode/',
    '<rootDir>/build/',
    '<rootDir>/node_modules/',
    '<rootDir>/docker/',
    '<rootDir>/dist/',
    '<rootDir>/src/components/sample',
    '<rootDir>/src/plugins/',
    '<rootDir>/src/languages/',
    '<rootDir>/coverage/',
    '<rootDir>/src/apps/sample/',
  ],
  moduleNameMapper: {
    '^components(.*)$': '<rootDir>/src/components$1',
    '^assets(.*)$': '<rootDir>/src/assets$1',
    '^plugins(.*)$': '<rootDir>/src/plugins$1',
    '^api(.*)$': '<rootDir>/src/api$1',
    '^utils(.*)$': '<rootDir>/src/utils$1',
    '^mixins(.*)$': '<rootDir>/src/mixins$1',
    '^configs(.*)$': '<rootDir>/src/configs$1',
    '^stores(.*)$': '<rootDir>/src/stores$1',
    '^routers(.*)$': '<rootDir>/src/routers$1',
    '^languages(.*)$': '<rootDir>/src/languages$1',

    '^__utils__(.*)$': '<rootDir>/tests/unit/__utils__$1',
    '^__mocks__(.*)$': '<rootDir>/tests/__mocks__$1',
  },
  collectCoverageFrom: ['**/*.{js,jsx,vue}'],

  setupFiles: ['<rootDir>/jest.init.js'],

  testMatch: ['**/*.spec.(js|jsx|ts|tsx)|**/__tests__/*.(js|jsx|ts|tsx)'],

  testURL: 'http://localhost:8886/',
}
