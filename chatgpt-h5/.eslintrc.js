module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/typescript/recommended',
    '@vue/prettier',
    '@vue/prettier/@typescript-eslint'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  rules: {
    // "no-extra-semi": 2, //禁止多余的冒号
    // "generator-star-spacing": "off",
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off'
    // "prettier/prettier": [
    //   "warn",
    //   {
    //     singleQuote: true
    //   }
    // ]
  }
}
