<template>
  <el-table-column :label="label" :width="width">
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
export default {
  props: {
    label: String,
    prop: String,
    width: Number,
    trueText: String,
    falseText: String,
  },
  methods: {
    booleanText(bool) {
      return bool ? this.trueText : this.falseText
    },
    booleanClass(bool) {
      return bool ? 'true' : 'false'
    },
  },
}
</script>

<style lang="scss">
.column-boolean {
  & > span:before {
    display: inline-block;
    width: 0.4em;
    height: 0.4em;
    margin-right: 0.3em;
    vertical-align: middle;
    border-radius: 50%;
    content: '';
  }
  & > span.true:before {
    background: #ee5c57;
  }
  & > span.false:before {
    background: #aabbce;
  }
}
</style>
