package studit.ui.schedule;

import java.util.List;
import studit.domain.TimeSlot;

public interface OnCompleteListener {
    void onComplete(List<TimeSlot> selectedSlots);
}