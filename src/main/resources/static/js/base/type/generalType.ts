type ErrorResponse = {
    statusError: number;
    message: string;
    timestamp: string;
    notificationType: NotificationType
}

type Employee = {
    id: number;
    name: string;
    subDivision: subDivision;
    role: string;
    isActive: boolean;
    chatId: number;
}

type subDivision = {
    id: number;
    name: string;
    code: string;
}

type SubDivision = {
    id: number;
    code: string;
    name: string;
}

type DocumentBormash = {
    id: string;
    name: string;
    files: DocumentFile[];
}

type DocumentFile = {
    id: string;
    baseFileName: string;
    type: string;
}

type Image = {
    id: string;
    name: string;
    data: string;
    mainlink: string;
}

