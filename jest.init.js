import { config } from '@vue/test-utils'

config.mocks['$t'] = () => {}

global.matchMedia =
  global.matchMedia ||
  function() {
    return {
      matches: false,
      addListener: function() {},
      removeListener: function() {},
    }
  }
