package fluffysnow.idearly.competition.domain;

import fluffysnow.idearly.common.BaseEntity;
import fluffysnow.idearly.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "competition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Competition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    @JoinColumn(name = "host_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member host;

    public Competition(String name, String description, LocalDateTime startDatetime, LocalDateTime endDatetime, Member host) {
        this.name = name;
        this.description = description;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.host = host;
    }
}
