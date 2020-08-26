<template>
  <vue-datepicker
    :format="'yyyy-MM-dd'"
    :pickerName="pickerName"
    :highlighted="highlighted"
    :useUtc="true"
  >
  </vue-datepicker>
</template>

<script>
export default {
  name: 'Datepicker',
  props: {
    pickerName: {
      type: String,
      default: '',
      required: true,
    },
    highlighted: {
      type: Object,
    },
  },
  mounted() {},
}
</script>
