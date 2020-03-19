<template>
  <popover
    trigger="click"
    placement="bottom"
    :width="196"
    popperClass="header-lnb-selector"
  >
    <ul>
      <li>
        <button>
          리모트원격솔루션
        </button>
      </li>
    </ul>

    <button slot="reference" class="header-workspace-selector">
      리모트원격솔루션
    </button>
  </popover>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'LnbButton',
  components: {
    Popover,
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
