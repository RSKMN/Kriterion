import { 
  FileText, 
  FileSpreadsheet, 
  Download, 
  Filter,
  TrendingUp,
  PieChart,
  History
} from 'lucide-react';
import React, { useState } from 'react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import { 
  Card, 
  CardContent, 
  CardDescription, 
  CardHeader, 
  CardTitle,
  CardFooter 
} from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { exportService } from '@/services/api/export.service';
import { downloadFile } from '@/utils/download';

const ReportsPage: React.FC = () => {
  const [isExportingCsv, setIsExportingCsv] = useState(false);
  const [isDownloadingMonthly, setIsDownloadingMonthly] = useState(false);
  const [isDownloadingYearly, setIsDownloadingYearly] = useState(false);
  const [isBackingUp, setIsBackingUp] = useState(false);

  // Filter states
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const handleFullBackup = async () => {
    setIsBackingUp(true);
    try {
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/backups/generate`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (!response.ok) throw new Error('Backup failed');
      
      const blob = await response.blob();
      downloadFile(blob, `kriterion_backup_${new Date().toISOString().split('T')[0]}.json`);
      toast.success('Full system backup generated successfully');
    } catch (error) {
      console.error('Backup failed:', error);
      toast.error('Failed to generate system backup.');
    } finally {
      setIsBackingUp(false);
    }
  };

  const handleCsvExport = async () => {
    setIsExportingCsv(true);
    try {
      const blob = await exportService.exportTransactionsCsv({ startDate, endDate });
      downloadFile(blob, `transactions_export_${new Date().toISOString().split('T')[0]}.csv`);
      toast.success('CSV Export successful');
    } catch (error) {
      console.error('CSV Export failed:', error);
      toast.error('Failed to export CSV. Please try again.');
    } finally {
      setIsExportingCsv(false);
    }
  };

  const handleMonthlyReport = async () => {
    setIsDownloadingMonthly(true);
    try {
      const blob = await exportService.downloadMonthlyReport();
      downloadFile(blob, `monthly_report_${new Date().toLocaleString('default', { month: 'long' })}_${new Date().getFullYear()}.pdf`);
      toast.success('Monthly Report generated');
    } catch (error) {
      console.error('Monthly Report failed:', error);
      toast.error('Failed to generate monthly report.');
    } finally {
      setIsDownloadingMonthly(false);
    }
  };

  const handleYearlyReport = async () => {
    setIsDownloadingYearly(true);
    try {
      const blob = await exportService.downloadYearlyReport();
      downloadFile(blob, `yearly_report_${new Date().getFullYear()}.pdf`);
      toast.success('Yearly Report generated');
    } catch (error) {
      console.error('Yearly Report failed:', error);
      toast.error('Failed to generate yearly report.');
    } finally {
      setIsDownloadingYearly(false);
    }
  };

  return (
    <div className="container max-w-6xl mx-auto py-8 px-4">
      <div className="flex flex-col gap-2 mb-8">
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">Reports & Exports</h1>
        <p className="text-slate-500">Download your financial data and generate professional reports.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {/* Monthly PDF Card */}
        <Card className="relative overflow-hidden border-blue-100 bg-gradient-to-br from-white to-blue-50/30">
          <CardHeader>
            <div className="h-10 w-10 rounded-lg bg-blue-100 flex items-center justify-center mb-2">
              <TrendingUp className="h-5 w-5 text-blue-600" />
            </div>
            <CardTitle>Monthly Report</CardTitle>
            <CardDescription>Comprehensive PDF summary of your spending and income for this month.</CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-slate-600">
            Includes category breakdowns, top spending items, and monthly trends.
          </CardContent>
          <CardFooter>
            <Button 
              className="w-full bg-blue-600 hover:bg-blue-700" 
              onClick={handleMonthlyReport}
              disabled={isDownloadingMonthly}
            >
              {isDownloadingMonthly ? 'Generating...' : (
                <>
                  <FileText className="h-4 w-4 mr-2" />
                  Download PDF
                </>
              )}
            </Button>
          </CardFooter>
        </Card>

        {/* Yearly PDF Card */}
        <Card className="border-purple-100 bg-gradient-to-br from-white to-purple-50/30">
          <CardHeader>
            <div className="h-10 w-10 rounded-lg bg-purple-100 flex items-center justify-center mb-2">
              <PieChart className="h-5 w-5 text-purple-600" />
            </div>
            <CardTitle>Yearly Review</CardTitle>
            <CardDescription>A deep dive into your financial year. Available in PDF.</CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-slate-600">
            Year-over-year comparisons and long-term spending habits analysis.
          </CardContent>
          <CardFooter>
            <Button 
              variant="outline" 
              className="w-full border-purple-200 text-purple-600 hover:bg-purple-50"
              onClick={handleYearlyReport}
              disabled={isDownloadingYearly}
            >
              {isDownloadingYearly ? 'Generating...' : (
                <>
                  <FileText className="h-4 w-4 mr-2" />
                  Download PDF
                </>
              )}
            </Button>
          </CardFooter>
        </Card>

        {/* CSV Export Card */}
        <Card className="border-green-100 bg-gradient-to-br from-white to-green-50/30">
          <CardHeader>
            <div className="h-10 w-10 rounded-lg bg-green-100 flex items-center justify-center mb-2">
              <FileSpreadsheet className="h-5 w-5 text-green-600" />
            </div>
            <CardTitle>Raw Data (CSV)</CardTitle>
            <CardDescription>Export your transactions to a CSV file for use in Excel or other tools.</CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-slate-600">
            Clean, formatted data including merchant names, categories, and payment methods.
          </CardContent>
          <CardFooter>
            <Button 
              variant="outline" 
              className="w-full border-green-200 text-green-600 hover:bg-green-50"
              onClick={handleCsvExport}
              disabled={isExportingCsv}
            >
              {isExportingCsv ? 'Exporting...' : (
                <>
                  <Download className="h-4 w-4 mr-2" />
                  Export CSV
                </>
              )}
            </Button>
          </CardFooter>
        </Card>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        {/* Full Backup Section */}
        <Card className="border-slate-200">
          <CardHeader>
            <div className="flex items-center gap-2">
              <History className="h-5 w-5 text-slate-400" />
              <CardTitle className="text-lg">Full Data Backup</CardTitle>
            </div>
            <CardDescription>Export your entire Kriterion account data as a JSON file.</CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-slate-600">
            A full backup includes all transactions, categories, budgets, and recurring schedules. 
            This file can be used to restore your data in the future or migrate between accounts.
          </CardContent>
          <CardFooter className="flex gap-3">
            <Button 
              variant="outline" 
              className="flex-1"
              onClick={handleFullBackup}
              disabled={isBackingUp}
            >
              {isBackingUp ? 'Generating...' : (
                <>
                  <Download className="h-4 w-4 mr-2" />
                  Generate JSON Backup
                </>
              )}
            </Button>
          </CardFooter>
        </Card>

        {/* Cloud Sync Preview */}
        <Card className="border-dashed border-slate-200 bg-slate-50/50">
          <CardHeader>
            <div className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5 text-slate-300" />
              <CardTitle className="text-lg text-slate-400">Cloud Sync (Coming Soon)</CardTitle>
            </div>
            <CardDescription>Automatic cloud synchronization and multi-device support.</CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-slate-400">
            Securely sync your financial data across all your devices using end-to-end encryption. 
            Cloud sync will be available in the next major update.
          </CardContent>
        </Card>
      </div>

      {/* Advanced Export Filters */}
      <Card className="mb-8">
        <CardHeader>
          <div className="flex items-center gap-2">
            <Filter className="h-5 w-5 text-slate-400" />
            <CardTitle className="text-lg">Export Filters</CardTitle>
          </div>
          <CardDescription>Refine the data range for your CSV export.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <Label htmlFor="start-date">Start Date</Label>
              <Input 
                id="start-date" 
                type="date" 
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="focus:ring-blue-500"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="end-date">End Date</Label>
              <Input 
                id="end-date" 
                type="date" 
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="focus:ring-blue-500"
              />
            </div>
          </div>
        </CardContent>
        <CardFooter className="border-t bg-slate-50/50 px-6 py-4 rounded-b-lg">
          <p className="text-xs text-slate-500 flex items-center gap-1">
            <History className="h-3 w-3" />
            Your filters will apply to all future CSV downloads in this session.
          </p>
        </CardFooter>
      </Card>
    </div>
  );
};

export default ReportsPage;
