export type DueStatus = 'overdue' | 'due-soon' | 'upcoming'

export const calculateDueStatus = (nextRunDate: string): DueStatus => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const dueDate = new Date(nextRunDate)
  dueDate.setHours(0, 0, 0, 0)
  
  const diffTime = dueDate.getTime() - today.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) {
    return 'overdue'
  }
  
  if (diffDays <= 3) {
    return 'due-soon'
  }
  
  return 'upcoming'
}

export const getDaysRemaining = (nextRunDate: string): number => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const dueDate = new Date(nextRunDate)
  dueDate.setHours(0, 0, 0, 0)
  
  const diffTime = dueDate.getTime() - today.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}
