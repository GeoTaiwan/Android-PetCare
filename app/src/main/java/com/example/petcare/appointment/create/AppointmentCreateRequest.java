package com.example.petcare.appointment.create;
public class AppointmentCreateRequest {

    // 為了確保 Gson 序列化時欄位名稱正確，推薦使用首字母小寫
    // 後端的 [FromBody] 預設可以不分大小寫自動對應
    private int petID;
    private String InstitutionID;
    private String serviceType;
    private String selectedDate;
    private String selectedTime;

    // 建構子，方便我們在 Activity 中快速組裝
    public AppointmentCreateRequest(int petID, String InstitutionID, String serviceType, String selectedDate, String selectedTime) {
        this.petID = petID;
        this.InstitutionID = InstitutionID;
        this.serviceType = serviceType;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
    }

    // Getter 與 Setter (如果需要讀取的話)
    public int getPetID() {
        return petID;
    }

    public void setPetID(int petID) {
        this.petID = petID;
    }

    public String getInstitutionID() {
        return InstitutionID;
    }

    public void setInstitutionID(String institutionID) {
        this.InstitutionID = institutionID;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }
}
