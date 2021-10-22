package Entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "battle_info", schema = "farid_db", catalog = "")
public class BattleInfoEntity {
    private int battleId;
    private String battleMode;
    private String battleWinner;
    private Date submissionDate;

    @Id
    @Column(name = "battle_id", nullable = false)
    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    @Basic
    @Column(name = "battle_mode", nullable = false, length = 150)
    public String getBattleMode() {
        return battleMode;
    }

    public void setBattleMode(String battleMode) {
        this.battleMode = battleMode;
    }

    @Basic
    @Column(name = "battle_winner", nullable = false, length = 200)
    public String getBattleWinner() {
        return battleWinner;
    }

    public void setBattleWinner(String battleWinner) {
        this.battleWinner = battleWinner;
    }

    @Basic
    @Column(name = "submission_date", nullable = true)
    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BattleInfoEntity that = (BattleInfoEntity) o;

        if (battleId != that.battleId) return false;
        if (battleMode != null ? !battleMode.equals(that.battleMode) : that.battleMode != null) return false;
        if (battleWinner != null ? !battleWinner.equals(that.battleWinner) : that.battleWinner != null) return false;
        if (submissionDate != null ? !submissionDate.equals(that.submissionDate) : that.submissionDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = battleId;
        result = 31 * result + (battleMode != null ? battleMode.hashCode() : 0);
        result = 31 * result + (battleWinner != null ? battleWinner.hashCode() : 0);
        result = 31 * result + (submissionDate != null ? submissionDate.hashCode() : 0);
        return result;
    }
}
