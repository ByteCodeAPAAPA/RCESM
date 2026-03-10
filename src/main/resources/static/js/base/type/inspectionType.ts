type InspectionIn = {
    id: number;
    subDivision: SubDivision;
    dateInspection: string;
    type: string;
    violation: InspectionViolationIn[];
    haveSecondInspection: boolean;
    primaryInspectionId: number;
}
type InspectionViolationIn = {
    id: string;
    createdBy: Employee;
    createdDate: string;
    inspectionId: number;
    description: string;
    criteria: string;
    score: number;
    subDivision: SubDivision;
    status: string;
}
