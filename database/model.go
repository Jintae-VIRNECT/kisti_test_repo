package database

import (
	"RM-RecordServer/data"
	"time"

	"github.com/biezhi/gorm-paginator/pagination"
	"github.com/jinzhu/gorm"
	_ "github.com/jinzhu/gorm/dialects/mysql"
	"github.com/spf13/viper"
)

type RecordingFileDB struct {
	db *gorm.DB
}

type RecordingFile struct {
	ID          uint64           `gorm:"primary_key;AUTO_INCREMENT;not_null"`
	RecordingID data.RecordingID `gorm:"not null;unique;unique_index"`
	SessionID   data.SessionID   `gorm:"not null"`
	WorkspaceID data.WorkspaceID `gorm:"not null"`
	UserID      string           `gorm:"not null"`
	Filename    string           `gorm:"not null" sql:"type:VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci"`
	Duration    int              `gorm:"not null"`
	Size        int              `gorm:"not null"`
	Resolution  string           `gorm:"not null"`
	Framerate   int              `gorm:"not null"`
	MetaData    string
	CreatedDate time.Time `gorm:"not null"`
	CreatedAt   time.Time
	UpdatedAt   time.Time
	DeletedAt   *time.Time `sql:"index"`
}

func NewTable(dbDriver string, parameter string) *RecordingFileDB {
	db, err := gorm.Open(dbDriver, parameter)
	if err != nil {
		panic("failed to connect database: " + err.Error())
	}

	if viper.GetBool("general.devMode") {
		db.LogMode(true)
	}

	db.AutoMigrate(RecordingFile{})

	return &RecordingFileDB{db}
}

func (m *RecordingFileDB) Create(id data.RecordingID, sessionID data.SessionID, workspaceID data.WorkspaceID, userID string, filename string, duration int, size int, resolution string, framerate int, metaData string, createdAt time.Time) (*RecordingFile, error) {
	record := &RecordingFile{
		RecordingID: id,
		SessionID:   sessionID,
		WorkspaceID: workspaceID,
		UserID:      userID,
		Filename:    filename,
		Duration:    duration,
		Size:        size,
		Resolution:  resolution,
		Framerate:   framerate,
		MetaData:    metaData,
		CreatedDate: createdAt,
	}
	if err := m.db.Create(record).Error; err != nil {
		return nil, err
	}

	return record, nil
}

func (m *RecordingFileDB) Delete(filter *data.Filter) (int64, error) {
	var result *gorm.DB
	if filter == nil {
		result = m.db.Unscoped().Delete(&RecordingFile{})
	} else {
		tx := m.db
		if filter.RecordingID != nil {
			tx = tx.Where(&RecordingFile{RecordingID: *filter.RecordingID})
		}
		if filter.WorkspaceID != nil {
			tx = tx.Where(&RecordingFile{WorkspaceID: *filter.WorkspaceID})
		}
		result = tx.Unscoped().Delete(&RecordingFile{})
	}

	if result.Error != nil {
		return 0, result.Error
	}

	return result.RowsAffected, nil
}

func (m *RecordingFileDB) Select(filter *data.Filter) ([]RecordingFile, int, error) {
	records := []RecordingFile{}

	if filter == nil {
		m.db.Find(&records)
		return records, 0, nil
	}

	tx := m.db
	if filter.Filename != nil {
		tx = tx.Where("filename LIKE ?", "%"+*filter.Filename+"%")
	}
	if filter.RecordingID != nil {
		tx = tx.Where(&RecordingFile{RecordingID: *filter.RecordingID})
	}
	if filter.SessionID != nil {
		tx = tx.Where(&RecordingFile{SessionID: *filter.SessionID})
	}
	if filter.WorkspaceID != nil {
		tx = tx.Where(&RecordingFile{WorkspaceID: *filter.WorkspaceID})
	}
	for _, t := range filter.CreatedAt {
		if t.Op == "ge" {
			tx = tx.Where("created_at >= ?", t.Time)
		}
		if t.Op == "gt" {
			tx = tx.Where("created_at > ?", t.Time)
		}
		if t.Op == "le" {
			tx = tx.Where("created_at <= ?", t.Time)
		}
		if t.Op == "lt" {
			tx = tx.Where("created_at < ?", t.Time)
		}
	}

	if filter.Page != nil && filter.Limit != nil {
		param := &pagination.Param{
			DB:    tx,
			Page:  *filter.Page,
			Limit: *filter.Limit,
		}

		if filter.OrderBy != nil {
			param.OrderBy = []string{*filter.OrderBy}
		}

		p := pagination.Paging(param, &records)
		return records, p.TotalPage, nil
	}

	if filter.OrderBy != nil {
		tx = tx.Order(*filter.OrderBy)
	}
	if filter.Limit != nil {
		tx = tx.Limit(*filter.Limit)
	}
	tx.Find(&records)
	return records, 0, nil
}
