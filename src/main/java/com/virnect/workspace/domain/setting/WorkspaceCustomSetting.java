package com.virnect.workspace.domain.setting;

import com.virnect.workspace.domain.TimeEntity;
import com.virnect.workspace.domain.workspace.Workspace;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "workspace_custom_setting")
@Entity
@Getter
@Audited
public class WorkspaceCustomSetting extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_custom_setting_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "value", nullable = false)
    private SettingValue value;


    @ManyToOne(targetEntity = Setting.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_id")
    @NotAudited
    private Setting setting;

    @ManyToOne(targetEntity = Workspace.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    @NotAudited
    private Workspace workspace;

    @Builder
    public WorkspaceCustomSetting(SettingValue settingValue, Setting setting, Workspace workspace) {
        this.value = settingValue;
        this.setting = setting;
        this.workspace = workspace;
    }

    public void setValue(SettingValue value) {
        this.value = value;
    }


}
