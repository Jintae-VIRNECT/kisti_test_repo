<template>
  <el-form
    class="sub-process-form"
    ref="form"
    :model="form"
    label-width="70px"
    style="width: 550px"
  >
    <el-form-item label="name">
      <span>{{ form.name }}</span>
    </el-form-item>
    <el-form-item label="date">
      <el-col :span="11">
        <el-date-picker
          placeholder="startDate"
          v-model="form.startDate"
        ></el-date-picker>
      </el-col>
      <el-col class="line" :span="2">-</el-col>
      <el-col :span="11">
        <el-date-picker
          placeholder="endDate"
          v-model="form.endDate"
        ></el-date-picker>
      </el-col>
    </el-form-item>
    <el-form-item label="worker">
      <el-input v-model="form.workerUUID"></el-input>
    </el-form-item>
    <el-button @click="submit(form)">
      {{ buttonName }}
    </el-button>
  </el-form>
</template>

<script>
export default {
  props: {
    form: Object,
    buttonName: String,
  },
  methods: {
    submit(form) {
      this.$emit('submit', form)
    },
  },
}
</script>
