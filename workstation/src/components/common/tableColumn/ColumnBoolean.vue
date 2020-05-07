<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-boolean">
        <span :class="booleanClass(scope.row[prop])">
          {{ booleanText(scope.row[prop]) }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
function check(bool) {
  return bool && bool.toLowerCase() !== 'no'
}

export default {
  props: {
    label: String,
    prop: String,
    width: Number,
    trueText: String,
    falseText: String,
    sortable: [Boolean, String],
  },
  methods: {
    booleanText(bool) {
      return check(bool) ? this.trueText : this.falseText
    },
    booleanClass(bool) {
      return check(bool) ? 'true' : 'false'
    },
  },
}
</script>

<style lang="scss">
.column-boolean {
  & > span {
    color: #566173;
  }
  & > span:before {
    display: inline-block;
    width: 6px;
    height: 6px;
    margin-right: 0.3em;
    vertical-align: middle;
    background: #566173;
    border-radius: 1em;
    content: '';
  }
  // true
  & > span.true {
    color: #186ae2;
  }
  & > span.true:before {
    background: #186ae2;
  }
}
</style>
