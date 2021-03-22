import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnDate from '@/components/common/tableColumn/ColumnDate'
import ColumnUser from '@/components/common/tableColumn/ColumnUser'
import ColumnBoolean from '@/components/common/tableColumn/ColumnBoolean'
import ColumnRole from '@/components/common/tableColumn/ColumnRole'
import ColumnProgress from '@/components/common/tableColumn/ColumnProgress'
import ColumnStatus from '@/components/common/tableColumn/ColumnStatus'
import ColumnCount from '@/components/common/tableColumn/ColumnCount'
import ColumnDropdown from '@/components/common/tableColumn/ColumnDropdown'
import ColumnPrice from '@/components/common/tableColumn/ColumnPrice'
import ColumnPlan from '@/components/common/tableColumn/ColumnPlan'

export default {
  components: {
    ColumnDefault,
    ColumnDate,
    ColumnUser,
    ColumnBoolean,
    ColumnRole,
    ColumnProgress,
    ColumnStatus,
    ColumnCount,
    ColumnDropdown,
    ColumnPrice,
    ColumnPlan,
  },
  mounted() {
    // 사파리 테이블 버그
    document
      .querySelectorAll('.el-table__body')
      .forEach(table => (table.style.tableLayout = 'auto'))
    setTimeout(() => {
      document
        .querySelectorAll('.el-table__body')
        .forEach(table => (table.style.tableLayout = 'fixed'))
    }, 10)
  },
}
