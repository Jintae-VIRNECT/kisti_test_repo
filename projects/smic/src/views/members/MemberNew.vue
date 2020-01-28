<template lang="pug">
  el-card.box-card
    el-form(ref='form' :model='form' label-width='120px')
      el-row
        el-col(:span="4")
          p 워크스페이스 멤버 생성하기
        el-col(:span="10")
          el-form-item(label='생성할 워크스페이스 멤버 계정')
            el-input(v-model='form.userId')
          el-form-item(label='비밀번호')
            el-input(placeholder='Please input password' v-model='form.password' show-password='')
          el-form-item(label='라이센스')
            el-select(v-model='form.select.value' placeholder='Select')
              el-option(v-for='item in form.select.options' :key='item.value' :label='item.label' :value='form.select.value')
          el-form-item(label='워크스페이스')
            el-select(v-model='form.workspace.value' placeholder='Select')
              el-option(v-for='item in form.workspace.options' :key='item.value' :label='item.label' :value='form.workspace.value')
          el-form-item(label='그룹')
            el-select(v-model='form.group.value' placeholder='Select')
              el-option(v-for='item in form.group.options' :key='item.value' :label='item.label' :value='form.group.value')
        el-col(:span="10")
          el-form-item(label='이름')
            el-input(v-model='form.username')
          el-form-item(label='계정 설명')
            el-input(v-model='form.description')

      el-form-item
        el-button(type='primary' @click='onSubmit') Create

</template>

<script>
export default {
  data() {
    return {
      form: {
        userId: null,
        username: null,
        description: null,
        password: null,
        select: {
          options: [
            {
              value: 'Option1',
              label: 'Option1',
            },
            {
              value: 'Option2',
              label: 'Option2',
            },
            {
              value: 'Option3',
              label: 'Option3',
            },
            {
              value: 'Option4',
              label: 'Option4',
            },
            {
              value: 'Option5',
              label: 'Option5',
            },
          ],
        },
        workspace: {
          options: [
            {
              value: 'Option1',
              label: 'Option1',
            },
            {
              value: 'Option2',
              label: 'Option2',
            },
            {
              value: 'Option3',
              label: 'Option3',
            },
            {
              value: 'Option4',
              label: 'Option4',
            },
            {
              value: 'Option5',
              label: 'Option5',
            },
          ],
        },
        group: {
          options: [
            {
              value: 'Option1',
              label: 'Option1',
            },
            {
              value: 'Option2',
              label: 'Option2',
            },
            {
              value: 'Option3',
              label: 'Option3',
            },
            {
              value: 'Option4',
              label: 'Option4',
            },
            {
              value: 'Option5',
              label: 'Option5',
            },
          ],
          value: '',
        },
      },
    }
  },
  methods: {
    onSubmit() {},
  },
}
</script>
