module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es6: true
  },
  extends: [
    "eslint:recommended",
    "plugin:vue/essential",
    "prettier",
    "plugin:prettier/recommended"
  ],
  globals: {
    Atomics: "readonly",
    SharedArrayBuffer: "readonly",
    process: "readonly",
    require: "readonly"
  },
  parserOptions: {
    parser: "babel-eslint",
    ecmaVersion: 2018,
    sourceType: "module"
  },
  plugins: ["prettier"],
  rules: {
    "prettier/prettier": [
      "error",
      // 아래 규칙들은 개인 선호에 따라 prettier 문법 적용
      // https://prettier.io/docs/en/options.html
      {
        singleQuote: true,
        semi: false,
        useTabs: false,
        tabWidth: 2,
        trailingComma: "all",
        bracketSpacing: true,
        arrowParens: "avoid",
        vueIndentScriptAndStyle: false
      }
    ],
    "no-console": process.env.NODE_ENV === "production" ? "error" : "off"
  },
  overrides: [
    {
      files: ["build/*.js"],
      globals: {
        process: "readonly",
        module: "readonly",
        __dirname: "readonly"
      }
    }
  ]
};
