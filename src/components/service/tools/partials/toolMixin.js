import { mapGetters, mapActions } from 'vuex'
import ToolButton from 'ToolButton'
import ToolPicker from 'ToolPicker'

export default {
  components: {
    ToolButton,
    ToolPicker,
  },
  filters: {},
  computed: {
    ...mapGetters(['action', 'tools']),
  },
  methods: {
    ...mapActions(['setAction', 'setTool']),
  },
}
