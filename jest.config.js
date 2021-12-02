module.exports = {
  roots: ['<rootDir>/tests/'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '^~/(.*)$': '<rootDir>/src/$1',
    '^vue$': 'vue/dist/vue.common.js',
    '^.+\\.svg$': 'jest-svg-transformer',
    '^__utils__(.*)$': '<rootDir>/tests/__utils__$1',
    '^__mocks__(.*)$': '<rootDir>/tests/__mocks__$1',
  },
  moduleFileExtensions: ['js', 'vue', 'json'],
  transform: {
    '^.+\\.js$': 'babel-jest',
    '.*\\.(vue)$': 'vue-jest',
    'vee-validate/dist/rules': 'babel-jest',
  },
  collectCoverage: false,
  collectCoverageFrom: [
    '<rootDir>/components/**/*.vue',
    '<rootDir>/pages/**/*.vue',
    '<rootDir>/pages/*.vue',
  ],
  testEnvironment: 'jsdom',
  testPathIgnorePatterns: [
    '<rootDir>/node_modules/',
    '<rootDir>/node_modules/(?!vee-validate/dist/rules)',
  ],
  setupFiles: ['<rootDir>/tests/jest.init.js'],
}
