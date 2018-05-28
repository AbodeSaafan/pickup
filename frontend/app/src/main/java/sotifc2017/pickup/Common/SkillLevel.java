package sotifc2017.pickup.Common;

import sotifc2017.pickup.R;

/**
 * Created by asaafan on 5/28/2018.
 */

public class SkillLevel {
    public static SkillLevelEnum GetSkillLevelFromRating(int rating){
        switch (rating){
            case 1:
            case 2:
                return SkillLevelEnum.Beginner;
            case 3:
            case 4:
                return SkillLevelEnum.Rookie;
            case 5:
            case 6:
                return SkillLevelEnum.Intermediate;
            case 7:
            case 8:
                return SkillLevelEnum.HighSchoolLevel;
            case 9:
            case 10:
                return SkillLevelEnum.CollegeLevelAndAbove;
            default:
                return SkillLevelEnum.None;
        }
    }

    public static int GetFriendlyTextResourceId(SkillLevelEnum skillLevel){
        switch (skillLevel){
            case Beginner:
                return R.string.skill_beginner;
            case Rookie:
                return R.string.skill_rookie;
            case Intermediate:
                return R.string.skill_intermediate;
            case HighSchoolLevel:
                return R.string.skill_highschool;
            case CollegeLevelAndAbove:
                return R.string.skill_college;
            case None:
            default:
                return R.string.none;
        }
    }

    public static int GetFriendlyTextResourceId(int skillRating){
        SkillLevelEnum skillLevel = GetSkillLevelFromRating(skillRating);
        switch (skillLevel){
            case Beginner:
                return R.string.skill_beginner;
            case Rookie:
                return R.string.skill_rookie;
            case Intermediate:
                return R.string.skill_intermediate;
            case HighSchoolLevel:
                return R.string.skill_highschool;
            case CollegeLevelAndAbove:
                return R.string.skill_college;
            case None:
            default:
                return R.string.none;
        }
    }
}
