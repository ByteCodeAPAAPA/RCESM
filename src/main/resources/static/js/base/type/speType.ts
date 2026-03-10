type SpeIn = {
    id: number;
    version: number;
    name: string;
    type: string;
    outNumber: string;
    accuracyClass: string;
    limitMeasurement: string;
    subDivision: SubDivision;
    employee: Employee;
    mark: string;
    datePreparation: string;
    dateVerification: string;
    certificateNumber: string;
    periodicity: number;
    documentId: string;
    status: string;
    color: Color;
    organization: string;
}

type OrganizationSPE = {
    name: string;
    position: string;
    verifier: string;
}